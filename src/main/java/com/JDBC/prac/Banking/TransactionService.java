
package com.JDBC.prac.Banking;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactions(int customerID) {
        return transactionRepository
                .findByAccountIDOrderByTimestampDesc(customerID);
    }
}
