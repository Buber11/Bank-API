package main.BankApp.service.contact;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import main.BankApp.model.user.Contact;
import main.BankApp.request.contact.ContactRequest;
import main.BankApp.response.ContactResponse;
import main.BankApp.repository.ContactRepository;
import main.BankApp.model.user.UserAccount;
import main.BankApp.common.Loggable;
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
    private final Function<HttpServletRequest, Long> getUserIdFromJwt = req -> (Long) req.getAttribute("id");

    @Override
    @Loggable
    public void save(ContactRequest contactRequest, HttpServletRequest request) {
        Long userId = extractUserId(request);
        contactRepository.findByNumberAccount(contactRequest.numberAccount())
                .ifPresentOrElse(
                        contact -> updateExistingContact(contact),
                        () -> saveNewContact(contactRequest, userId)
                );
    }

    @Override
    @Loggable
    public List<ContactResponse> getAllContacts(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return contactRepository.findByUserAccount_UserId(userId).stream()
                .sorted(Comparator.comparing(Contact::getNumberOfUse).reversed()
                        .thenComparing(Contact::getDateOfLastUse))
                .map(this::convertToContactResponse)
                .collect(Collectors.toList());
    }

    private Long extractUserId(HttpServletRequest request) {
        return getUserIdFromJwt.apply(request);
    }

    private void updateExistingContact(Contact contact) {
        contact.setNumberOfUse(contact.getNumberOfUse() + 1);
        contact.setDateOfLastUse(LocalDate.now());
        contactRepository.save(contact);
    }

    private void saveNewContact(ContactRequest contactRequest, Long userId) {
        Contact newContact = createContact(contactRequest);
        UserAccount userAccount = userService.getUser(userId);
        newContact.setUserAccount(userAccount);
        contactRepository.save(newContact);
    }

    private Contact createContact(ContactRequest contactRequest) {
        return Contact.builder()
                .name(contactRequest.name())
                .numberAccount(contactRequest.numberAccount())
                .dateOfLastUse(LocalDate.now())
                .numberOfUse(1L)
                .build();
    }

    private ContactResponse convertToContactResponse(Contact contact) {
        return new ContactResponse(
                contact.getContactId(),
                contact.getName(),
                contact.getNumberAccount()
        );
    }
}
