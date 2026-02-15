package cz.gyarabProject.database.kb.repository;

import cz.gyarabProject.database.kb.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
