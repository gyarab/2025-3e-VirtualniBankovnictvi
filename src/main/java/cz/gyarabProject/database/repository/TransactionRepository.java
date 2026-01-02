package cz.gyarabProject.database.repository;

import cz.gyarabProject.database.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
