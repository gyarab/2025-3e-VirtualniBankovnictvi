package cz.gyarabProject.database.kb.repository;

import cz.gyarabProject.database.kb.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    boolean existsBalanceByBankAccountId(String bankAccountId);
}
