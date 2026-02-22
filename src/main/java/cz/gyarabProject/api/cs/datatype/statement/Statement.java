package cz.gyarabProject.api.cs.datatype.statement;

import java.time.LocalDate;

public record Statement (
        String accountStatementId,
        int year,
        int month,
        int sequenceNumber,
        String period,
        LocalDate dateFrom,
        LocalDate dateTo,
        Format formats
) {
    public record Format (String availability, String format) {}
}
