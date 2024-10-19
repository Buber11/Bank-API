package main.BankApp.model.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CurrencyRate {

    private String table;
    private String currency;
    private String code;
    private Rate[] rates;

    @Data
    public static class Rate {
        private String no;
        private String effectiveDate;
        private double mid;

    }

}