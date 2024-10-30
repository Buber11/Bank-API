package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.model.account.Transaction;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.account.AccountService;
import main.BankApp.service.account.TransactionService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/create")
    public ResponseEntity createBankAccount(HttpServletRequest request){
       accountService.createAccount(request);
       return ResponseUtil.buildSuccessResponse("The account was successfully created.");
    }

    @GetMapping("/get")
    public ResponseEntity getAllAccounts(HttpServletRequest request){
        var accounts = accountService.getAlAccounts(request);
        return ResponseUtil.buildSuccessResponse(accounts);
    }


    @PostMapping("/transaction/own")
    public ResponseEntity doTransaction(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request){
        accountService.makeOwnTransaction(request, transactionRequest);
        return ResponseUtil.buildSuccessResponse("Transaction is done");
    }

}
