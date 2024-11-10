package main.BankApp.service.currency;

import main.BankApp.model.account.CurrencyRate;

public interface CurrencyService {
    public CurrencyRate getCurrencyRate(String currency);
    public CurrencyRate getCurrencyRate(String currency, String date);
}
