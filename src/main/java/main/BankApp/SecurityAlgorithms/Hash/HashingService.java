package main.BankApp.SecurityAlgorithms.Hash;

public interface HashingService {
    String hash(String data);
    boolean matches(String rawData, String hashedData);
}
