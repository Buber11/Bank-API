package main.BankApp.repository;

import main.BankApp.model.account.Account;
import main.BankApp.model.account.AccountStatus;
import main.BankApp.model.account.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    List<Account> findByUserAccount_UserId(Long id);
    Optional<Account> findByAccountNumberAndUserAccount_UserId(String accountNumber, Long userId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List findByAccountStatus(AccountStatus accountStatus);

}
