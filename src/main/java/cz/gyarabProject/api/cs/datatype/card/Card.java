package cz.gyarabProject.api.cs.datatype.card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Card(
        String id,
        LocalDate expiryDate,
        LocalDate validFromDate,
        String cardHolderName,
        String maskedPAN,
        String cardStatus,
        boolean virtualCard,
        Account account,
        boolean activeMulticurrency,
        List<Account> multicurrencyAccounts,
        Image imageDate,
        Limit limits
){
    public record Account (
            String id,
            String bankCode,
            String accountPrefix,
            String accountNumber,
            String iban,
            String currency
    ) {}
    public record Image (
            String imageKey,
            String imageUrl
    ) {}
    public record Limit (
            long atmLimit,
            long posLimit,
            long ecommerceLimit
    ) {}
}
