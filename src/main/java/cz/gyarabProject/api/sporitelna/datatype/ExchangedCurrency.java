package cz.gyarabProject.api.sporitelna.datatype;

import java.time.LocalDateTime;

public record ExchangedCurrency(String from,
                                String to,
                                double amount,
                                String type,
                                double result,
                                LocalDateTime validFrom,
                                boolean buy) {
}
