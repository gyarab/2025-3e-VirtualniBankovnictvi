package cz.gyarabProject.api.cs.datatype.card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CardTransaction(
        String id,
        String reservationId,
        Amount transactionAmount,
        Amount accountCurrencyAmount,
        String creditDebitIndicatior,
        String paymentMethod,
        Account account,
        String cardholderName,
        Instant cardTransactionDateTime,
        LocalDate bookingDay,
        LocalDate bookingType,
        LocalDate bookingTypeDescription,
        String maskedPAN,
        String maskedVirtualCard,
        MerchantInfo merchantInfo,
        RemittanceInformation remittanceInformation
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
    public record RemittanceInformation (
            String constantSymbol,
            String variableSymbol,
            String specificSymbol
    ) {}
}
