package main.BankApp.NBPApi;

import lombok.RequiredArgsConstructor;
import main.BankApp.app.Loggable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NBP.api.url}")
    private String NBP_API_URL;

    @Override
    @Loggable
    public CurrencyRate getCurrencyRate(String currency) {
        String url = UriComponentsBuilder.fromHttpUrl(NBP_API_URL)
                .pathSegment("A", currency)
                .queryParam("format", "json")
                .toUriString();
        try {
            return restTemplate.getForObject(url, CurrencyRate.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Loggable
    public CurrencyRate getCurrencyRate(String currency, String date) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);

            String formattedDate = dateFormat.format(parsedDate);

            String url = UriComponentsBuilder.fromHttpUrl(NBP_API_URL)
                    .pathSegment("A", currency, formattedDate)
                    .queryParam("format", "json")
                    .toUriString();

            return restTemplate.getForObject(url, CurrencyRate.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}