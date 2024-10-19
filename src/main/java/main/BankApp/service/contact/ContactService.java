package main.BankApp.service.contact;

import jakarta.servlet.http.HttpServletRequest;

import main.BankApp.request.contact.ContactRequest;

import java.util.List;

public interface ContactService {
    void save(ContactRequest contactRequest, HttpServletRequest request);
    List getAllContacts(HttpServletRequest request);

}
