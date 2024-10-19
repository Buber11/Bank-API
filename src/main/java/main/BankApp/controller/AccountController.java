package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.account.AccountService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    AccountService accountService;

    @GetMapping("/create")
    public ResponseEntity createBankAccount(HttpServletRequest request){
       accountService.createAccount(request);
       return ResponseUtil.buildSuccessResponse("The account was successfully created.");
    }

    @GetMapping("/get")
    public ResponseEntity getAllAccounts(HttpServletRequest request){
        return null;
    }

    @GetMapping("/transaction/getAll")
    public ResponseEntity getAllTransaction(HttpServletRequest request){
        return null;
    }

    @PostMapping("/transaction/save")
    public ResponseEntity saveTransaction(HttpServletRequest httpServletRequest, @RequestBody TransactionRequest transactionRequest){
        return null;
    }

}
