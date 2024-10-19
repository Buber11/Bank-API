package main.BankApp.repository;

import main.BankApp.model.user.Contact;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userAccountRepository;

    private UserAccount testUserAccount;

    @BeforeEach
    public void setUp() {

        testUserAccount = UserAccount.builder()
                .username("john_doe")
                .passwordHash("hashed_password")
                .email("john.doe@example.com")
                .status(StatusAccount.ACTIVE)
                .failedLoginAttempts(0)
                .lastLogin(LocalDateTime.now())
                .twoFactorEnabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .consentToCommunication(true)
                .isBusinessAccount(false)
                .hmac("some_hmac_value")
                .build();

        userAccountRepository.save(testUserAccount);


        Contact contact1 = Contact.builder()
                .name("Alice")
                .numberAccount("123456790")
                .numberOfUse(5L)
                .dateOfLastUse(LocalDate.now())
                .userAccount(testUserAccount)
                .build();

        Contact contact2 = Contact.builder()
                .name("Bob")
                .numberAccount("987654321")
                .numberOfUse(3L)
                .dateOfLastUse(LocalDate.now())
                .userAccount(testUserAccount)
                .build();

        contactRepository.save(contact1);
        contactRepository.save(contact2);
    }

    @Test
    @Rollback(value = true)
    public void testFindByUserAccount_UserId() {

        List<Contact> contacts = contactRepository.findByUserAccount_UserId(testUserAccount.getUserId());


        assertThat(contacts).isNotEmpty();
        assertThat(contacts).hasSize(2);
        assertThat(contacts.get(0).getName()).isEqualTo("Alice");
        assertThat(contacts.get(1).getName()).isEqualTo("Bob");
    }

    @Test
    @Rollback(value = true)
    public void testFindByNumberAccount() {

        Optional<Contact> contact = contactRepository.findByNumberAccount("123456790");


        assertThat(contact).isPresent();
        assertThat(contact.get().getNumberAccount()).isEqualTo("123456790");


        Optional<Contact> nonExistentContact = contactRepository.findByNumberAccount("000000000");
        assertThat(nonExistentContact).isNotPresent();
    }

}