package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.BankApp.util.ResponseUtil;
import main.BankApp.request.contact.ContactRequest;
import main.BankApp.service.contact.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/contacts")
    public ResponseEntity<String> save(@Valid @RequestBody ContactRequest contactRequest, HttpServletRequest request){
        contactService.save(contactRequest, request);
        System.out.println(contactRequest);
        return ResponseUtil.buildSuccessResponse("The Contact is sucessfuly added");
    }

    @GetMapping("/contacts")
    public  ResponseEntity getAll(HttpServletRequest request){
        return ResponseUtil.buildSuccessResponse(
                contactService.getAllContacts(request)
        );
    }

}
