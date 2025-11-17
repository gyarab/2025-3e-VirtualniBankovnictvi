package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePage {

    @GetMapping(value="/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
