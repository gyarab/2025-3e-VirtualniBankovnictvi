package cz.gyarabProject.database.kb.service;

import cz.gyarabProject.database.kb.entity.AccountId;
import cz.gyarabProject.database.kb.repository.AccountIdRepository;

public class AccountIdService {
    private final AccountIdRepository accountId;

    public AccountIdService(AccountIdRepository accountId) {
        this.accountId = accountId;
    }

    public AccountId add(Long userId, String bankAccountId) {
        if (userId == null || bankAccountId == null) {
            throw new IllegalArgumentException("UserId or bankAccountId cannot null.");
        } else if (accountId.existsByBankAccountId(bankAccountId)) {
            throw new IllegalStateException(bankAccountId + "  already exists.");
        }
        return accountId.save(new AccountId(userId, bankAccountId));
    }
}
