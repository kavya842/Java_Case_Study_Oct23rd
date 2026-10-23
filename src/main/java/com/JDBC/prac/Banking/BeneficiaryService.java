
package com.JDBC.prac.Banking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Transactional
    public Beneficiary addBeneficiary(
            int customerID,
            String name,
            String accountNumber,
            String bankDetails) {

        if (name == null || name.isBlank()
                || accountNumber == null
                || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Name and account number are required.");
        }

        Beneficiary beneficiary = new Beneficiary(
                customerID,
                name.trim(),
                accountNumber.trim(),
                bankDetails == null ? "" : bankDetails.trim()
        );

        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(int customerID) {
        return beneficiaryRepository.findByCustomerID(customerID);
    }

    @Transactional
    public void deleteBeneficiary(
            int beneficiaryID, int customerID) {

        Beneficiary beneficiary = beneficiaryRepository
                .findByBeneficiaryIDAndCustomerID(
                        beneficiaryID, customerID)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Beneficiary not found."));

        beneficiaryRepository.delete(beneficiary);
    }
}

