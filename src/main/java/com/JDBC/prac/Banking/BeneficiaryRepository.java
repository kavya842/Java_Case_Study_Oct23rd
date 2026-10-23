
package com.JDBC.prac.Banking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Integer> {

    List<Beneficiary> findByCustomerID(int customerID);

    Optional<Beneficiary> findByBeneficiaryIDAndCustomerID(
            int beneficiaryID, int customerID);
}
