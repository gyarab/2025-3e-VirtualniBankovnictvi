package cz.gyarabProject.database.repository;

import cz.gyarabProject.database.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    boolean existsBalanceByBankAccountId(Long bankAccountId);
}
