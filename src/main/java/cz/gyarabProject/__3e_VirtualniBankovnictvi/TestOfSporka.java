package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.api.cs.ExchangeRates;
import cz.gyarabProject.api.cs.OAuth2;
import cz.gyarabProject.api.cs.Places;
import cz.gyarabProject.api.cs.account.Payment;
import cz.gyarabProject.api.cs.datatype.Language;
import cz.gyarabProject.api.cs.datatype.Token;
import cz.gyarabProject.database.entity.User;
import cz.gyarabProject.database.repository.AccountIdRepository;
import cz.gyarabProject.database.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/test/sporitelna")
public class TestOfSporka {
    private final UserRepository user;
    private final HttpSession session;
    private final OAuth2 oAuth2;
    private final ExchangeRates rates;
    private final Places places;
    private final Payment payment;

    public TestOfSporka(
            UserRepository user,
            HttpSession session,
            OAuth2 oAuth2,
            ExchangeRates rates,
            Places places,
            Payment payment
    ) {
        this.user = user;
        this.session = session;
        this.oAuth2 = oAuth2;
        this.rates = rates;
        this.places = places;
        this.payment = payment;
    }

    @GetMapping(value="")
    public String logIn(HttpServletResponse response, HttpServletRequest request) {
        return "<a href=\"http://localhost:8080/test/sporitelna/logout\">logout</a>";
    }

    @GetMapping(value="/logout")
    public String logout() throws IOException, InterruptedException {
        Long id = (Long) session.getAttribute("userId");
        oAuth2.logout(id);
        Token token = user.getUserById(id).getTokenCS();
        return "Logout was successful!<br>Refresh token: " + token.getRefresh() + "<br>Access token: " + token.getAccess() +
                "<br><a href=\"http://localhost:8080/test/sporitelna\"> new tokens</a>";
    }

    @GetMapping(value="/exchangeRate")
    public String exchangeRate() throws IOException, InterruptedException {
        return rates.exchangeRates(LocalDate.parse("2015-03-22"), LocalDate.parse("2015-03-23"),
                "USD", Language.cs, false).toString();
    }

    @GetMapping(value="/exchange")
    public String exchange() throws IOException, InterruptedException {
        return rates.exchange("USD", "CZK", "CASH", 1000, true).toString();
    }

    @GetMapping(value="/currencies")
    public String currencies() throws IOException, InterruptedException {
        return rates.getCurrencies(Language.cs, false).toString();
    }

    @GetMapping(value="/cross")
    public String cross() throws IOException, InterruptedException {
        return rates.cross(LocalDate.parse("2015-03-22"), LocalDate.parse("2015-03-23"),
                "CZK", "USD").toString();
    }

    @GetMapping(value="/times")
    public String times() throws IOException, InterruptedException {
        return rates.times(LocalDate.parse("2015-03-22")).toString();
    }

    @GetMapping(value="/health-check")
    public String healthCheck() throws IOException, InterruptedException {
        return String.valueOf(rates.isAvailable());
    }

    @GetMapping(value="/atm")
    public String atmById() throws IOException, InterruptedException {
        Long id = (Long) session.getAttribute("userId");
        Token token = user.getUserById(id).getTokenCS();
        return places.atms(15, token).toString();
    }

    @GetMapping(value = "/payment")
    public String payment() throws IOException, InterruptedException {
        Long id = (Long) session.getAttribute("userId");
        Token token = user.getUserById(id).getTokenCS();
        return payment.getAccountInfo(42, 2, "iban", "desc", token).toString();
    }
}
