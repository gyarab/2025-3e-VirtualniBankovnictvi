package cz.gyarabProject.database.kb.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.time.Instant;

@Getter
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "transaction_booking_date_idx", columnList = "booking_date")
})
public class Transaction implements BigDecimalNormalizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, name = "bank_account_id")
    private String bankAccountId;
    @Column(name = "last_updated")
    private Instant lastUpdated;
    @Column(name = "account_type")
    private String accountType;
    @Column(name = "entry_reference")
    private String entryReference;
    @Column(name = "iban")
    private String iban;
    @Column(name = "credit_debit_indicator")
    private Integer creditDebitIndicator;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "booking_date")
    private Date bookingDate;
    @Column(name = "value_date")
    private Date valueDate;
    @Column(name = "status")
    private String status;
    @Column(name = "additional_transaction_information")
    private String additionalInfo;
    @Column(name = "bank_transaction_code")
    private String bankTransactionCode;
    @Column(name = "bank_transaction_issuer")
    private String bankTransactionIssuer;
    @Column(name = "amount_value", precision = 19, scale = 4)
    private BigDecimal amountValue;
    @Column(name = "amount_currency")
    private String amountCurrency;
    @Column(name = "instructed_value", precision = 19, scale = 4)
    private BigDecimal instructedValue;
    @Column(name = "instructed_currency")
    private String instructedCurrency;
    @Column(name = "counter_party_iban")
    private String counterPartyIban;
    @Column(name = "counter_party_name")
    private String counterPartyName;
    @Column(name = "counter_party_account_number")
    private String counterPartyAccountNumber;
    @Column(name = "counter_party_bank_bic")
    private String counterPartyBankBic;
    @Column(name = "counter_party_bank_code")
    private String counterPartyBankCode;
    @Column(name = "references_account_servicer")
    private String referencesAccountServicer;
    @Column(name = "references_reciever")
    private String referencesReciever;
    @Column(name = "references_description")
    private String referencesDescription;

    protected Transaction() {}

    public Transaction(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Transaction(String bankAccountId, TransactionDto builder) {
        this.bankAccountId = bankAccountId;
        this.lastUpdated = builder.lastUpdated;
        this.accountType = builder.accountType;
        this.entryReference = builder.entryReference;
        this.iban = builder.iban;
        this.creditDebitIndicator = builder.creditDebitIndicator;
        this.transactionType = builder.transactionType;
        this.bookingDate = builder.bookingDate;
        this.valueDate = builder.valueDate;
        this.status = builder.status;
        this.additionalInfo = builder.additionalInfo;
        this.bankTransactionCode = builder.bankTransactionCode.code;
        this.bankTransactionIssuer = builder.bankTransactionCode.issuer;
        this.amountValue = BigDecimalNormalizer.normalize(builder.amount.value);
        this.amountCurrency = builder.amount.currency;
        this.instructedValue = BigDecimalNormalizer.normalize(builder.instructed.value);
        this.instructedCurrency = builder.instructed.currency;
        this.counterPartyIban = builder.counterParty.iban;
        this.counterPartyName = builder.counterParty.name;
        this.counterPartyAccountNumber = builder.counterParty.accountNumber;
        this.counterPartyBankBic = builder.counterParty.bankBic;
        this.counterPartyBankCode = builder.counterParty.bankCode;
        this.referencesAccountServicer = builder.references.accountService;
        this.referencesReciever = builder.references.reciever;
        this.referencesDescription = builder.references.description;
    }
}
