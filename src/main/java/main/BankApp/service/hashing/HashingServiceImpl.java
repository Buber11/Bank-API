package main.BankApp.service.hashing;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HashingServiceImpl implements HashingService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String data) {
        return passwordEncoder.encode(data);
    }

    public boolean matches(String rawData, String hashedData) {
        return passwordEncoder.matches(rawData, hashedData);
    }
}
