package cz.gyarabProject.database.kb.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.time.Instant;

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

    public Long getId() { return id; }
    public String getBankAccountId() { return bankAccountId; }
    public Instant getLastUpdated() { return lastUpdated; }
    public String getaccountType() { return accountType; }
    public String getEntryReference() { return entryReference; }
    public String getIban() { return iban; }
    public Integer getCreditDebitIndicator() { return creditDebitIndicator; }
    public String getTransactionType() { return transactionType; }
    public Date getBookingDate() { return bookingDate; }
    public Date getValueDate() { return valueDate; }
    public String getStatus() { return status; }
    public String getAdditionalInfo() { return additionalInfo; }
    public String getBankTransactionCode() { return bankTransactionCode; }
    public String getBankTransactionIssuer() { return bankTransactionIssuer; }
    public BigDecimal getAmountValue() { return amountValue; }
    public String getAmountCurrency() { return amountCurrency; }
    public BigDecimal getInstructedValue() { return instructedValue; }
    public String getInstructedCurrency() { return instructedCurrency; }
    public String getCounterPartyIban() { return counterPartyIban; }
    public String getCounterPartyName() { return counterPartyName; }
    public String getCounterPartyAccountNumber() { return counterPartyAccountNumber; }
    public String getCounterPartyBankBic() { return counterPartyBankBic; }
    public String getCounterPartyBankCode() { return counterPartyBankCode; }
    public String getReferencesAccountServicer() { return referencesAccountServicer; }
    public String getReferencesReciever() { return referencesReciever; }
    public String getReferencesDescription() { return referencesDescription; }
}
