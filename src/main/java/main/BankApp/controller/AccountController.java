package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.AccountModel;
import main.BankApp.dto.TransactionModel;
import main.BankApp.model.account.Currency;
import main.BankApp.request.transaction.MultipleTransactionRequest;
import main.BankApp.request.transaction.SingleTransactionRequest;
import main.BankApp.service.account.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @PostMapping("/accounts")
    public ResponseEntity createBankAccount(HttpServletRequest request){
       accountService.createAccount(request);
       return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity getAllAccounts(HttpServletRequest request){
        var accounts = accountService.getAlAccounts(request);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{account-number}/transaction/{status}")
    public ResponseEntity<Page<TransactionModel>> getTransaction(
            @PathVariable("account-number") String accountNumber,
            @PathVariable("status") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(sortBy).reverse());
        Page<TransactionModel> pageTransaction = accountService.getTransactions(pageable,accountNumber,status);
        return ResponseEntity.ok(pageTransaction);
    }

    @PostMapping("/transactions")
    public ResponseEntity doTransaction(@Valid @RequestBody SingleTransactionRequest transactionRequest, HttpServletRequest request){
        accountService.makeOwnSingleTransaction(request, transactionRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transactions-group")
    public ResponseEntity doTransactions(@Valid @RequestBody MultipleTransactionRequest transactionRequest, HttpServletRequest request){
        accountService.doOwnMultipleTransaction(request,transactionRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/accounts/{account-number}/currency")
    public ResponseEntity<AccountModel> changeCurrency(
            @PathVariable("account-number") String numberAccount,
            @RequestParam("cur") Currency currency
    ) {
        AccountModel updatedAccount = accountService.convertCurrency(numberAccount, currency);
        return ResponseEntity.ok(updatedAccount);
    }

}
