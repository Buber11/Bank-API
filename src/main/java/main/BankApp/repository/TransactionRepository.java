package main.BankApp.repository;

import main.BankApp.model.account.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Page<Transaction> findByPayeeAccount_AccountNumber(String accountNumber, Pageable pageable);
    Page<Transaction> findByHostAccount_AccountNumber(String accountNumber, Pageable pageable);
}
