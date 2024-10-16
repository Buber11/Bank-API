package main.BankApp.User.Contact.Controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.Response.ResponseUtil;
import main.BankApp.User.Contact.Model.Request.ContactRequest;
import main.BankApp.User.Contact.Repository.ContactRepository;
import main.BankApp.User.Contact.Service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ContactRequest contactRequest, HttpServletRequest request){
        contactService.save(contactRequest, request);
        return ResponseUtil.buildSuccessResponse("The Contact is sucessfuly added");
    }

    @GetMapping("/get")
    public  ResponseEntity getAll(HttpServletRequest request){
        return ResponseUtil.buildSuccessResponse(
                contactService.getAllContacts(request)
        );
    }

}
