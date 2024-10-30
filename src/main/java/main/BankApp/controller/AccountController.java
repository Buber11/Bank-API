package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.model.account.Transaction;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.account.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @PostMapping("/account")
    public ResponseEntity createBankAccount(HttpServletRequest request){
       accountService.createAccount(request);
       return ResponseEntity.noContent().build();
    }

    @GetMapping("/account")
    public ResponseEntity getAllAccounts(HttpServletRequest request){
        var accounts = accountService.getAlAccounts(request);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("account/{account-number}/transaction/{status}")
    public ResponseEntity<Page<Transaction>> getTransaction(
            @PathVariable("account-number") String accountNumber,
            @PathVariable("status") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(sortBy));
        Page<Transaction> pageTransaction = accountService.getTransactions(pageable,accountNumber,status);
        return ResponseEntity.ok(pageTransaction);
    }

    @PostMapping("/transaction")
    public ResponseEntity doTransaction(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request){
        accountService.makeOwnTransaction(request, transactionRequest);
        return ResponseEntity.noContent().build();
    }

}
