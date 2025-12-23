package cz.gyarabProject.database.service;

import cz.gyarabProject.database.entity.Balance;
import cz.gyarabProject.database.repository.BalanceRepository;

public class BalanceService {
    private final BalanceRepository balance;

    public BalanceService(BalanceRepository balance) {
        this.balance =  balance;
    }

    public Balance add(Long bankAccountId) {
        return add(bankAccountId, null);
    }

    public Balance add(Long bankAccountId, Balance.Builder builder) {
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

    public Balance.Builder getBalanceBuilder() {
        return new Balance.Builder();
    }
}
