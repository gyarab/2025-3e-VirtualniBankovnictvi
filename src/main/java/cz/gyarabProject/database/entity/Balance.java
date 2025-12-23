package cz.gyarabProject.database.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, name = "bank_account_id")
    private Long bankAccountId;
    private String type;
    @Column(name = "credit_debit_indicator")
    private String creditDebit;
    @Column(name = "amount_value")
    private Long amountValue;
    @Column(name = "amount_currency")
    private String amountCurrency;
    @Column(name = "valid_at")
    private Instant validAt;
    @Column(name = "credit_line_value")
    private Long creditLineValue;
    @Column(name = "credit_line_currency")
    private String creditLineCurrency;

    protected Balance() {}

    public Balance(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Balance(Long bankAccountId, Builder builder) {
        this.bankAccountId = bankAccountId;
        this.type = builder.type;
        this.creditDebit = builder.creditDebit;
        this.amountValue = builder.amountValue;
        this.amountCurrency = builder.amountCurrency;
        this.validAt = builder.validAt;
        this.creditLineValue = builder.creditLineValue;
        this.creditLineCurrency = builder.creditLineCurrency;
    }

    public static class Builder {
        private String type;
        private String creditDebit;
        private Long amountValue;
        private String amountCurrency;
        private Instant validAt;
        private Long creditLineValue;
        private String creditLineCurrency;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder creditDebit(String creditDebit) {
            this.creditDebit = creditDebit;
            return this;
        }

        public Builder amountValue(Long amountValue) {
            this.amountValue = amountValue;
            return this;
        }

        public Builder amountCurrency(String amountCurrency) {
            this.amountCurrency = amountCurrency;
            return this;
        }

        public Builder validAt(Instant validAt) {
            this.validAt = validAt;
            return this;
        }

        public Builder creditLineValue(Long creditLineValue) {
            this.creditLineValue = creditLineValue;
            return this;
        }

        public Builder creditLineCurrency(String creditLineCurrency) {
            this.creditLineCurrency = creditLineCurrency;
            return this;
        }

        public Balance build(Long bankAccountId) {
            return new Balance(bankAccountId, this);
        }
    }

    public Long getId() { return id; }
    public Long getBankAccountId() { return bankAccountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCreditDebit() { return creditDebit; }
    public void setCreditDebit(String creditDebit) { this.creditDebit = creditDebit; }

    public Long getAmountValue() { return amountValue; }
    public void setAmountValue(Long amountValue) { this.amountValue = amountValue; }

    public String getAmountCurrency() { return amountCurrency; }
    public void setAmountCurrency(String amountCurrency) { this.amountCurrency = amountCurrency; }

    public Instant getValidAt() { return validAt; }
    public void setValidAt(Instant validAt) { this.validAt = validAt; }

    public Long getCreditLineValue() { return creditLineValue; }
    public void setCreditLineValue(Long creditLineValue) { this.creditLineValue = creditLineValue; }

    public String getCreditLineCurrency() { return creditLineCurrency; }
    public void setCreditLineCurrency(String creditLineCurrency) { this.creditLineCurrency = creditLineCurrency; }
}
