package main.BankApp.service.rsa;

public interface VaultService {

    String encrypt(String data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
}
