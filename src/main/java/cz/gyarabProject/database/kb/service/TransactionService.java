package cz.gyarabProject.database.kb.service;

import cz.gyarabProject.database.kb.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transaction;

    public TransactionService(TransactionRepository transaction) {
        this.transaction = transaction;
    }
}
