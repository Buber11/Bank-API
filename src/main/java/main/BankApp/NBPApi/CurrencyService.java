package main.BankApp.NBPApi;

import java.util.Date;

public interface CurrencyService {
    public CurrencyRate getCurrencyRate(String currency);
    public CurrencyRate getCurrencyRate(String currency, String date);
}
