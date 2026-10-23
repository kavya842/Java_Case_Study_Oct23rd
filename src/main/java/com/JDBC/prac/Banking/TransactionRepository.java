
package com.JDBC.prac.Banking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountIDOrderByTimestampDesc(
            int accountID);
}
