package cz.gyarabProject.database.kb.service;

import cz.gyarabProject.database.kb.entity.Balance;
import cz.gyarabProject.database.kb.entity.BalanceDto;
import cz.gyarabProject.database.kb.repository.BalanceRepository;

public class BalanceService {
    private final BalanceRepository balance;

    public BalanceService(BalanceRepository balance) {
        this.balance =  balance;
    }

    public Balance add(String bankAccountId) {
        return add(bankAccountId, null);
    }

    public Balance add(String bankAccountId, BalanceDto builder) {
        if (bankAccountId == null) {
            throw new IllegalArgumentException("BankAccountId cannot null.");
        } else if (balance.existsBalanceByBankAccountId(bankAccountId)) {
            throw new IllegalStateException(bankAccountId + " already exists.");
        }
        if (builder == null) {
            return balance.save(new Balance(bankAccountId));
        }
        return balance.save(new Balance(bankAccountId, builder));
    }

    public BalanceDto getBalanceDto() {
        return new BalanceDto();
    }
}
