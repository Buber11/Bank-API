package main.BankApp.repository;

import main.BankApp.model.user.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUserAccount_UserId(Long userId);
    Optional<Contact> findByNumberAccount(String numberAccount);

}
