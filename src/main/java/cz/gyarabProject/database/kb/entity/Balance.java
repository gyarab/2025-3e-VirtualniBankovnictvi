package cz.gyarabProject.database.kb.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "balances")
public class Balance implements BigDecimalNormalizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, name = "bank_account_id")
    private String bankAccountId;
    private String type;
    @Column(name = "credit_debit_indicator")
    private String creditDebit;
    @Column(name = "amount_value", precision = 19, scale = 4)
    private BigDecimal amountValue;
    @Column(name = "amount_currency")
    private String amountCurrency;
    @Column(name = "valid_at")
    private Instant validAt;
    @Column(name = "credit_line_value", precision = 19, scale = 4)
    private BigDecimal creditLineValue;
    @Column(name = "credit_line_currency")
    private String creditLineCurrency;

    protected Balance() {}

    public Balance(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Balance(String bankAccountId, BalanceDto builder) {
        this.bankAccountId = bankAccountId;
        this.type = builder.type;
        this.creditDebit = builder.creditDebit;
        this.amountValue = BigDecimalNormalizer.normalize(builder.amount.value);
        this.amountCurrency = builder.amount.currency;
        this.validAt = builder.validAt;
        this.creditLineValue = BigDecimalNormalizer.normalize(builder.creditLine.value);
        this.creditLineCurrency = builder.creditLine.currency;
    }

    public Long getId() { return id; }
    public String getBankAccountId() { return bankAccountId; }
    public String getType() { return type; }
    public String getCreditDebit() { return creditDebit; }
    public BigDecimal getAmountValue() { return amountValue; }
    public String getAmountCurrency() { return amountCurrency; }
    public Instant getValidAt() { return validAt; }
    public BigDecimal getCreditLineValue() { return creditLineValue; }
    public String getCreditLineCurrency() { return creditLineCurrency; }
}
