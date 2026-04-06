package cz.gyarabProject.api.cs;

import cz.gyarabProject.api.cs.datatype.Language;
import cz.gyarabProject.api.cs.datatype.Token;
import cz.gyarabProject.api.cs.datatype.exchangeRates.Currency;
import cz.gyarabProject.api.cs.datatype.exchangeRates.ExchangeRate;
import cz.gyarabProject.api.cs.datatype.place.ATM;
import cz.gyarabProject.api.cs.datatype.PageInfo;
import cz.gyarabProject.api.cs.datatype.Region;
import cz.gyarabProject.database.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/george")
@RequiredArgsConstructor // Lombok: Automaticky vytvoří konstruktor pro všechny 'private final' proměnné
public class GeorgeController {

    // Spring Boot sem automaticky dosadí (injectne) tvé existující komponenty
    private final ExchangeRates exchangeRates;
    private final Places places;
    private final OAuth2 oAuth2;
    private final UserRepository userRepository;

    // ---------------------------------------------------------
    // 1. PUBLIC API: Směnné kurzy a měny (Nepotřebuje OAuth2 Token klienta)
    // ---------------------------------------------------------

    @GetMapping("/kurzy")
    public List<ExchangeRate> getKurzy(
            @RequestParam(defaultValue = "CS") Language lang,
            @RequestParam(defaultValue = "false") boolean card) {
        try {
            // Pro ukázku stahujeme kurzy za posledních 7 dní
            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(7);
            
            return exchangeRates.exchangeRates(from, to, "EUR", lang, card);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při stahování kurzů: " + e.getMessage());
        }
    }

    @GetMapping("/meny")
    public List<Currency> getMeny(@RequestParam(defaultValue = "CS") Language lang) {
        try {
            return exchangeRates.getCurrencies(lang, false);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při stahování seznamu měn: " + e.getMessage());
        }
    }

    @GetMapping("/dostupnost")
    public String checkHealth() {
        try {
            boolean isUp = exchangeRates.isAvailable();
            return isUp ? "API směnných kurzů běží." : "API směnných kurzů hlásí problém.";
        } catch (Exception e) {
            return "Chyba připojení: " + e.getMessage();
        }
    }

    // ---------------------------------------------------------
    // 2. PRIVÁTNÍ API: Bankomaty a pobočky (Vyžaduje OAuth2 Token)
    // ---------------------------------------------------------

    @GetMapping("/bankomaty")
    public PageInfo<ATM> getBankomaty(
            HttpServletRequest request,
            @RequestParam String mesto,
            @RequestParam String lat,
            @RequestParam String lng) {
        try {
            // Získání Tokenu pro aktuálně přihlášeného uživatele (zajištěno přes Session a Interceptor)
            Token token = getUzivatelskyToken(request);

            // Volání tvé metody Places.atms()
            return places.atms(
                    mesto, lat, lng, "5", Region.PRAHA, "CZ", null, null, 
                    1, 10, null, Places.Detail.NORMAL, LocalDate.now(), token
            );
        } catch (Exception e) {
            throw new RuntimeException("Chyba při hledání bankomatů: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // 3. OAUTH2: Ruční správa (Login / Logout)
    // ---------------------------------------------------------

    @GetMapping("/login-url")
    public String getLoginUrl() {
        // Vrátí adresu, na kterou máš uživatele přesměrovat, aby se přihlásil přes George
        return oAuth2.authUri();
    }

    @PostMapping("/logout")
    public String odhlasit(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getSession().getAttribute("userId");
            if (userId != null) {
                oAuth2.logout(userId);
                request.getSession().invalidate(); // Zničení sezení
                return "Uživatel úspěšně odhlášen.";
            }
            return "Nikdo nebyl přihlášen.";
        } catch (Exception e) {
            throw new RuntimeException("Chyba při odhlašování: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // Pomocné metody
    // ---------------------------------------------------------

    /**
     * Vytáhne ID uživatele ze sezení a najde jeho přístupový token v databázi.
     */
    private Token getUzivatelskyToken(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("Uživatel není přihlášen v Session!");
        }
        return userRepository.getUserById(userId).getTokenCS();
    }
}