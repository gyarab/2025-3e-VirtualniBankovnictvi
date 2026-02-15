package cz.gyarabProject.database.kb.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class BalanceDto {
    public String type;
    public String creditDebit;
    public Amount amount;
    public Instant validAt;
    public CreditLine creditLine;

    public static class Amount {
        public BigDecimal value;
        public String currency;
    }

    public static class CreditLine {
        public BigDecimal value;
        public String currency;
    }
}
