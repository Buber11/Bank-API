package main.BankApp.SecurityAlgorithms.RSA;

public interface RSAService {

    String encrypt(String data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
}
