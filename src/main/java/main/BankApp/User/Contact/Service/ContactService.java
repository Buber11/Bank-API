package main.BankApp.User.Contact.Service;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.User.Contact.Model.DTO.ContactDecrypted;
import main.BankApp.User.Contact.Model.Entity.Contact;
import main.BankApp.User.Contact.Model.Request.ContactRequest;

import java.util.List;

public interface ContactService {
    void save(ContactRequest contactRequest, HttpServletRequest request);
    List<ContactDecrypted> getAllContacts(HttpServletRequest request);

}