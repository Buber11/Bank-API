package main.BankApp.service.contact;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import main.BankApp.model.user.Contact;
import main.BankApp.request.contact.ContactRequest;
import main.BankApp.response.ContactResponse;
import main.BankApp.repository.ContactRepository;
import main.BankApp.model.user.UserAccount;
import main.BankApp.annotation.Loggable;
import main.BankApp.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;
    private final Function<HttpServletRequest,Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");

    @Loggable
    public void save(ContactRequest contactRequest, HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);

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
    public List<ContactResponse> getAllContacts(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);

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
