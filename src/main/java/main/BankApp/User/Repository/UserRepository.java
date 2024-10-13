package main.BankApp.User.Repository;

import main.BankApp.User.ENTITY.UserAccount;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount,Long> {
    Optional<UserAccount> findByUsername(String username);

}
