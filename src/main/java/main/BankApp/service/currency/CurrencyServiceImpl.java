package main.BankApp.service.currency;

import lombok.RequiredArgsConstructor;
import main.BankApp.model.account.CurrencyRate;
import main.BankApp.common.Loggable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NBP.api.url}")
    private String NBP_API_URL;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @Loggable
    public CurrencyRate getCurrencyRate(String currency) {
        String url = buildUrl(currency, null);
        return fetchCurrencyRate(url);
    }

    @Override
    @Loggable
    public CurrencyRate getCurrencyRate(String currency, String date) {
        String formattedDate = parseAndFormatDate(date);
        String url = buildUrl(currency, formattedDate);
        return fetchCurrencyRate(url);
    }

    private String buildUrl(String currency, String date) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NBP_API_URL)
                .pathSegment("C", currency);
        if (date != null) {
            builder.pathSegment(date);
        }
        return builder.queryParam("format", "json").toUriString();
    }

    private String parseAndFormatDate(String date) {
        try {
            Date parsedDate = DATE_FORMAT.parse(date);
            return DATE_FORMAT.format(parsedDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd", e);
        }
    }

    private CurrencyRate fetchCurrencyRate(String url) {
        try {
            return restTemplate.getForObject(url, CurrencyRate.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch currency rate from NBP API", e);
        }
    }

}