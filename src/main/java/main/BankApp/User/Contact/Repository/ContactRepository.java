package main.BankApp.User.Contact.Repository;

import main.BankApp.User.Contact.Model.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUserAccount_UserId(long userId);
    Optional<Contact> findByNumberAccount(String numberAccount);

}
