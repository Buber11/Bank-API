package main.BankApp.repository;

import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount,Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    List<UserAccount> findByStatus(StatusAccount statusAccount);

}
