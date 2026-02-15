package cz.gyarabProject.database.kb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface BigDecimalNormalizer {
    int bigDecimalScale = 4;
    int bigDecimalPrecision = 19;

    static BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return null;
        }
        value.setScale(bigDecimalScale, RoundingMode.HALF_UP);
        if (value.precision() > bigDecimalPrecision) {
            throw new IllegalArgumentException("Value precision is greater than big decimal");
        }
        return value;
    }
}
