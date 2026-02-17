package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.api.sporitelna.ExchangeRates;
import cz.gyarabProject.api.sporitelna.OAuth2;
import cz.gyarabProject.api.sporitelna.Places;
import cz.gyarabProject.api.sporitelna.datatype.Language;
import cz.gyarabProject.api.sporitelna.datatype.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

@RestController
public class TestOfSporka {
    private final OAuth2 auth2;
    private final Token token;
    private final ExchangeRates rates;
    private final Places places;

    public TestOfSporka(OAuth2 auth2, Token token, ExchangeRates rates, Places places) {
        this.auth2 = auth2;
        this.token = token;
        this.rates = rates;
        this.places = places;
    }

    @GetMapping(value="sporitelna")
    public String logIn(HttpServletResponse response) throws IOException, InterruptedException {
        if (!token.isRefreshValid()) {
            String uri = auth2.authUri();
            response.sendRedirect(uri);
        }
        if (!token.isValid()) {
            auth2.newAccessToken();
        }
        return "<a href=\"http://localhost:8080/sporitelna/logout\">logout</a>";
    }

    @GetMapping(value="sporitelna/logout")
    public String logout() throws IOException, InterruptedException {
        auth2.logout();
        return "Logout was successful!<br>Refresh token: " + token.getRefresh() + "<br>Access token: " + token.getAccess();
    }

    @GetMapping(value="sporitelna/exchangeRate")
    public String exchangeRate() throws IOException, InterruptedException {
        return Arrays.toString(rates.exchangeRates(LocalDate.parse("2015-03-22"), LocalDate.parse("2015-03-23"),
                "USD", Language.cs, false));
    }

    @GetMapping(value="sporitelna/exchange")
    public String exchange() throws IOException, InterruptedException {
        return rates.exchange("USD", "CZK",
                "CASH", 1000, true).toString();
    }

    @GetMapping(value="sporitelna/currencies")
    public String currencies() throws IOException, InterruptedException {
        return Arrays.toString(rates.getCurrencies(Language.cs, false));
    }

    @GetMapping(value="sporitelna/cross")
    public String cross() throws IOException, InterruptedException {
        return Arrays.toString(rates.cross(LocalDate.parse("2015-03-22"), LocalDate.parse("2015-03-23"),
                "CZK", "USD"));
    }

    @GetMapping(value="sporitelna/times")
    public String times() throws IOException, InterruptedException {
        return Arrays.toString(rates.times(LocalDate.parse("2015-03-22")));
    }

    @GetMapping(value="sporitelna/health-check")
    public String healthCheck() throws IOException, InterruptedException {
        return String.valueOf(rates.isAvailable());
    }

    @GetMapping(value="sporitelna/atm")
    public String atmById() throws IOException, InterruptedException {
        return places.atms(15).toString();
    }
}
