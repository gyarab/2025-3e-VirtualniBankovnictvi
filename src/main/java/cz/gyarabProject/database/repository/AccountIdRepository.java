package cz.gyarabProject.database.repository;

import cz.gyarabProject.database.entity.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountIdRepository extends JpaRepository<AccountId, Long> {
    boolean existsByBankAccountId(String bankAccountId);
}
