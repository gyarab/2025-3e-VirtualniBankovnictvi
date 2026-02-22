package cz.gyarabProject.api.cs.datatype.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Response (
        String id,
        String eventType,
        Amount reservationAmount,
        Amount originalReservationAmount,
        String creditDebitIndicatior,
        Account account,
        Instant startDateTime,
        LocalDate expirationDate,
        String reservationState,
        String reservationType,
        String maskedPAN,
        MerchantInfo merchantInfo
) {
    public record Amount (
            double value,
            String currency
    ) {}
    public record Account (
            String id,
            String bankCode,
            String accountPrefix,
            String accountNumber,
            String iban,
            String currency
    ) {}
    public record MerchantInfo (
            @JsonProperty(value = "merchantName") String name,
            @JsonProperty(value = "merchantAdress") String adress,
            @JsonProperty(value = "merchantId") String id,
            @JsonProperty(value = "merchantCategoryCode") int categoryCode,
            @JsonProperty(value = "merchantShopCountry") String shopCountry
    ) {}
}
