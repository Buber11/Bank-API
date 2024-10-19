package main.BankApp.controller;

import lombok.RequiredArgsConstructor;
import main.BankApp.model.currency.CurrencyRate;
import main.BankApp.service.currency.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyRateController {

    private final CurrencyService currencyService;


    @GetMapping("/{currency}")
    public ResponseEntity<CurrencyRate> getCurrencyRate(@PathVariable String currency) {
        try {
            CurrencyRate rate = currencyService.getCurrencyRate(currency);
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/{currency}/{date}")
    public ResponseEntity<CurrencyRate> getCurrencyRateWithData(@PathVariable String currency,
                                                                @PathVariable String date) {
        CurrencyRate rate = currencyService.getCurrencyRate(currency,date);
        return ResponseEntity.ok(rate);
    }

}