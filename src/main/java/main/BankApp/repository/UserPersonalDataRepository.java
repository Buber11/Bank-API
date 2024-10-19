package main.BankApp.repository;

import main.BankApp.model.user.UserPersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserPersonalDataRepository extends JpaRepository<UserPersonalData,Long> {
    @Query("SELECT u.peselHash FROM UserPersonalData u")
    List<String> findAllPeselHash();
}
