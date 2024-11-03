package main.BankApp.repository;

import main.BankApp.model.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findBySessionId(String sessionId);

    List<Session> findByIsActive(Boolean active);

    Optional<Session> findByUserAccount_UserIdAndIsActive(Long userId, boolean active);


}