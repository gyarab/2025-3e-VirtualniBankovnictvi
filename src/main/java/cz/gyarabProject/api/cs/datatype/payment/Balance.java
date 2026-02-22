package cz.gyarabProject.api.cs.datatype.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Balance (
        Type type,
        Amount amount,
        Date date
) {
    public record Type (
            CodeOrProprietary codeOrProprietary
    ) {
        public record CodeOrProprietary (
                String code
        ) {}
    }
    public record Amount (
            double value, String currency
    ) {}
    public record Date (
            OffsetDateTime dateTime
    ) {}
}
