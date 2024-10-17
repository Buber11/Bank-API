package main.BankApp.User.Contact.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import main.BankApp.User.Contact.Model.Entity.Contact;
import main.BankApp.User.Contact.Model.Request.ContactRequest;
import main.BankApp.User.Contact.Model.Response.ContactResponse;
import main.BankApp.User.Contact.Repository.ContactRepository;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.Service.UserService;
import main.BankApp.app.Loggable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;

    @Loggable
    public void save(ContactRequest contactRequest, HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");

        Optional<Contact> existingContact = contactRepository.findByNumberAccount(contactRequest.numberAccount());
        if (existingContact.isPresent()) {
            Contact contact = existingContact.get();
            contact.setNumberOfUse(contact.getNumberOfUse() + 1);
            contact.setDateOfLastUse(LocalDate.now());
            contactRepository.save(contact);
        } else {
            Contact newContact = createContact(contactRequest);
            UserAccount userAccount = userService.get(userId);
            newContact.setUserAccount(userAccount);
            contactRepository.save(newContact);
        }
    }

    @Override
    @Loggable
    public List getAllContacts(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        return contactRepository.findByUserAccount_UserId(userId).stream()
                .sorted(Comparator.comparing(Contact::getNumberOfUse).reversed().thenComparing(Contact::getDateOfLastUse))
                .map(this::createContactResponse)
                .collect(Collectors.toList());
    }

    private Contact createContact(ContactRequest contactRequest) {
        return Contact.builder()
                .name(contactRequest.name())
                .numberAccount(contactRequest.numberAccount())
                .dateOfLastUse(LocalDate.now())
                .numberOfUse( Long.valueOf(1) )
                .build();
    }
    private ContactResponse createContactResponse(Contact contact) {
        return ContactResponse.builder()
                .name(contact.getName())
                .numberAccount(contact.getNumberAccount())
                .build();
    }
}
