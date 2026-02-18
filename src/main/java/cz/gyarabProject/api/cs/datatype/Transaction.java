package cz.gyarabProject.api.cs.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Transaction (
        String entryReference,
        String reservationId,
        Amount amount,
        String creditDebitIndicator,
        String status,
        Date bookingDate,
        Date valueDate,
        BankTransactionCode bankTransactionCode,
        EntryDetails entryDetails

) {
    public record Amount (
            double value, String currency
    ) {}
    public record Date (
            String date
    ) {}
    public record BankTransactionCode (
            Proprietary proprietary
    ) {
        public record Proprietary (
                long code, String issuer
        ) {}
    }
    public record EntryDetails(
            TransactionDetails transactionDetails
    ) {
        public record TransactionDetails(
                References references,
                AmountDetails amountDetails,
                Charges charges,
                RelatedParties relatedParies,
                RelatedAgents relatedAgents,
                RemittanceInformation remittanceInformation,
                String additionalTransactionInformation,
                String additionalRemittanceInformation,
                String additionalTransactionDescription
        ) {
            public record References (
                    String accountServicerReference, String endToTndIdentification, String chequeNumber
            ) {}
            public record AmountDetails (
                    InstructedAmount instructedAmount, CounterValueAmount counterValueAmount
            ) {
                public record InstructedAmount (
                        Amount amount
                ) {}
                public record CounterValueAmount (
                        Amount amount, CounterValueAmount.CurrencyExchange currencyExchange
                ) {
                    public record CurrencyExchange (
                            String sourceCurrency, String targetCurrency, double exchangeRate
                    ) {}
                }
            }
            public record Charges (
                    String bearer
            ) {}
            public record RelatedParties (
                    Debtor debtor,
                    DebtorAccount debtorAccount,
                    Creditor creditor,
                    CreditorAccount creditorAccount,
                    Proprietary proprietary
            ) {
                public record Debtor (
                        String name
                ) {}
                public record DebtorAccount (
                        Identification identification
                ) {}
                public record Creditor (
                        String name
                ) {}
                public record CreditorAccount (
                        Identification identification
                ) {}
                public record Proprietary (
                        Party party
                ) {
                    public record Party (
                            String name
                    ) {}
                }
                public record Identification (
                        String iban, Other other
                ) {
                    public record Other (
                            String identification
                    ) {}
                }
            }
            public record RelatedAgents (
                    CreditorAgent creditorAgent, DebtorAgent debtorAgent
            ) {
                public record CreditorAgent (
                        FinancialInstitutionIdentification financialInstitutionIdentification
                ) {}
                public record DebtorAgent (
                        FinancialInstitutionIdentification financialInstitutionIdentification
                ) {}
                public record FinancialInstitutionIdentification (
                        String bic
                ) {}
            }
            public record RemittanceInformation (
                    String unstructured, Structured structured
            ) {
                public record Structured (
                        CreditorReferenceInformation creditorReferenceInformation
                ) {
                    public record CreditorReferenceInformation (
                            List<String> reference
                    ) {}
                }
            }
        }
    }
}
