package main.BankApp.User.Repository;

import main.BankApp.User.ENTITY.Session;
import main.BankApp.User.ENTITY.UserAccount;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findBySessionId(String sessionId);

    List<Session> findByIsActive(boolean active);

    Optional<Session> findByUserAccount_UserIdAndIsActive(Long userId, boolean active);


}