package cz.gyarabProject.api.kb.datatype;

import java.time.LocalDate;

public class Statement {
    private final LocalDate issued;
    private final int sequenceNumber;
    private final int pageCount;
    private final long statementId;
    private final boolean archive;

    public Statement(LocalDate issued,
                     int sequenceNumber,
                     int pageCount,
                     long statementId,
                     boolean archive) {
        this.issued = issued;
        this.sequenceNumber = sequenceNumber;
        this.pageCount = pageCount;
        this.statementId = statementId;
        this.archive = archive;
    }

    public LocalDate getIssued() {
        return issued;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getPageCount() {
        return pageCount;
    }

    public long getStatementId() {
        return statementId;
    }

    public boolean getArchive() {
        return archive;
    }
}
