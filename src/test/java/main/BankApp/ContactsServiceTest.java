package main.BankApp;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.model.user.Contact;
import main.BankApp.response.ContactResponse;
import main.BankApp.repository.ContactRepository;
import main.BankApp.service.contact.ContactServiceImpl;
import main.BankApp.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ContactsServiceTest {
    @Autowired
    ContactServiceImpl contactService;

    @MockBean
    ContactRepository contactRepository;

    @MockBean
    HttpServletRequest request;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        contactService = new ContactServiceImpl(contactRepository, userService);
    }

    @Test
    public void testGetAllContacts() {

        List<Contact> contactList = List.of(
                Contact.builder()
                        .contactId(1L)
                        .name("John Doe")
                        .numberOfUse(10L)
                        .numberAccount("3131313131")
                        .build()
        );

        when(contactRepository.findByUserAccount_UserId(Long.valueOf(1))).thenReturn(contactList);
        when(request.getAttribute("id")).thenReturn(Long.valueOf(1));

        List<ContactResponse> contacts = contactService.getAllContacts(request);

        assertAll(
                () -> verify(contactRepository, times(1)).findByUserAccount_UserId(1L),
                () -> verify(request, times(1)).getAttribute("id"),
                () -> assertNotNull(contacts, "The result should not be null"),
                () -> assertEquals(1, contacts.size(), "The list should contain exactly one contact"),
                () -> assertEquals("John Doe", contacts.get(0).name(), "The contact's name should be John Doe"),
                () -> assertEquals("3131313131", contacts.get(0).numberAccount(), "The contact's account number should be 3131313131")
        );

    }

}


