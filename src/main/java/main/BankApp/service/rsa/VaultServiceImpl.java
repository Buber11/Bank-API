package main.BankApp.service.rsa;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Base64;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultTemplate vaultTemplate;

    @Value("${vault-nameKey}")
    private String keyName;


    public String encrypt(String data) throws Exception {

        String base64EncodedData = Base64.getEncoder().encodeToString(data.getBytes());


        VaultResponse response = vaultTemplate.write("transit/encrypt/" + keyName,
                Collections.singletonMap("plaintext", base64EncodedData));
        if (response == null || response.getData() == null) {
            throw new Exception("Encryption failed.");
        }


        return (String) response.getData().get("ciphertext");
    }


    public String decrypt(String encryptedData) throws Exception {

        VaultResponse response = vaultTemplate.write("transit/decrypt/" + keyName,
                Collections.singletonMap("ciphertext", encryptedData));
        if (response == null || response.getData() == null) {
            throw new Exception("Decryption failed.");
        }

        String decryptedData = (String) response.getData().get("plaintext");
        byte[] decoded = Base64.getDecoder().decode(decryptedData);
        return new String(decoded);
    }
}