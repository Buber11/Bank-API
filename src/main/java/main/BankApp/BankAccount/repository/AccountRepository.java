package main.BankApp.BankAccount.repository;

import main.BankApp.BankAccount.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
