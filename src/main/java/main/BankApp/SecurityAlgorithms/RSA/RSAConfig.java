package main.BankApp.SecurityAlgorithms.RSA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSAConfig {
    @Value("${public-key-rsa}")
    private String publicKey;
    @Value("${private-key-rsa}")
    private String privateKey;
    @Bean
    public RSAService rsaService() throws Exception {
        return new RSAServiceImpl(publicKey, privateKey);
    }
}