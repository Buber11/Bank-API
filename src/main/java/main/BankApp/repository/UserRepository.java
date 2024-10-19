package main.BankApp.repository;

import main.BankApp.model.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount,Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);

}
