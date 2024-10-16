package main.BankApp.User.Contact.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.Expection.RSAException;
import main.BankApp.SecurityAlgorithms.RSA.RSAService;
import main.BankApp.User.Contact.Model.DTO.ContactDecrypted;
import main.BankApp.User.Contact.Model.Entity.Contact;
import main.BankApp.User.Contact.Model.Request.ContactRequest;
import main.BankApp.User.Contact.Model.Response.ContactResponse;
import main.BankApp.User.Contact.Repository.ContactRepository;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final RSAService rsaService;
    private final UserService userService;

    @Override
    public void save(ContactRequest contactRequest, HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        Contact encryptedContact = encryptContact(contactRequest);
        UserAccount userAccount = userService.get(userId);
        encryptedContact.setUserAccount(userAccount);
        contactRepository.save(encryptedContact);
    }

    @Override
    public List<ContactDecrypted> getAllContacts(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        return contactRepository.findByUserAccount_UserId(userId).stream()
                .map(this::decryptContact)
                .sorted()
                .collect(Collectors.toList());
    }

    private Contact encryptContact(ContactRequest contactRequest) {
        try {
            return Contact.builder()
                    .name(rsaService.encrypt(contactRequest.name()))
                    .numberAccount(rsaService.encrypt(contactRequest.numberAccount()))
                    .dateOfLastUse(rsaService.encrypt(contactRequest.dateOfLastUse()))
                    .numberOfUse(rsaService.encrypt(contactRequest.numberOfUse()))
                    .build();
        } catch (Exception e) {
            throw new RSAException("Failed to encrypt contact", e);
        }
    }

    private ContactDecrypted decryptContact(Contact contact) {
        try {
            return ContactDecrypted.builder()
                    .contactId(contact.getContactId())
                    .name(rsaService.decrypt(contact.getName()))
                    .numberAccount(rsaService.decrypt(contact.getNumberAccount()))
                    .dateOfLastUse(rsaService.decrypt(contact.getDateOfLastUse()))
                    .numberOfUse(rsaService.decrypt(contact.getNumberOfUse()))
                    .build();
        } catch (Exception e) {
            throw new RSAException("Failed to decrypt contact", e);
        }
    }

    private ContactResponse createContactResponse(ContactDecrypted contactDecrypted) {
        return ContactResponse.builder()
                .name(contactDecrypted.getName())
                .numberAccount(contactDecrypted.getNumberAccount())
                .build();
    }
}