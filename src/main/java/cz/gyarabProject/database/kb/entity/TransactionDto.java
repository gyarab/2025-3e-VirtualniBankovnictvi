package cz.gyarabProject.database.kb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

public class TransactionDto {
    public Instant lastUpdated;
    public String accountType;
    public String entryReference;
    public String iban;
    public int creditDebitIndicator;
    public String transactionType;
    public Date bookingDate;
    public Date valueDate;
    public String status;
    public String additionalInfo;
    public BankTransactionCode bankTransactionCode;
    public Amount amount;
    public Instructed instructed;
    public CounterParty counterParty;
    public References references;

    public static class BankTransactionCode {
        public String code;
        public String issuer;
    }

    public static class Amount {
        public BigDecimal value;
        public String currency;
    }

    public static class Instructed {
        public String currency;
        public BigDecimal value;
    }

    public static class CounterParty {
        public String iban;
        public String name;
        @JsonProperty(value = "accountNo")
        public String accountNumber;
        public String bankBic;
        public String bankCode;
    }

    public static class References {
        public String accountService;
        public String reciever;
        @JsonProperty(value = "myDescription")
        public String description;
    }
}
