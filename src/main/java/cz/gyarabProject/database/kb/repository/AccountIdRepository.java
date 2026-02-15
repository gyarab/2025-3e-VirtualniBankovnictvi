package cz.gyarabProject.database.kb.repository;

import cz.gyarabProject.database.kb.entity.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountIdRepository extends JpaRepository<AccountId, Long> {
    boolean existsByBankAccountId(String bankAccountId);
}
