package main.BankApp;

import main.BankApp.service.rsa.RSAServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class RSAServiceImplTest {

    private RSAServiceImpl rsaService;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @BeforeEach
    public void setUp() throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        rsaService = new RSAServiceImpl(publicKeyString, privateKeyString);
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        String originalData = "Hello, RSA!";

        // Encrypt the original data
        String encryptedData = rsaService.encrypt(originalData);
        assertNotNull(encryptedData);
        assertNotEquals(originalData, encryptedData); // Ensure the data is encrypted

        // Decrypt the data
        String decryptedData = rsaService.decrypt(encryptedData);
        assertEquals(originalData, decryptedData); // Ensure the decrypted data matches the original
    }
    @Test
    public void testDecryptThrowsExceptionWithInvalidInput() {
        Exception exception = assertThrows(Exception.class, () -> {
            rsaService.decrypt("invalidBase64Data");
        });
        assertTrue(exception instanceof IllegalArgumentException || exception instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    public void testConstructorThrowsExceptionWithInvalidKeys() {
        assertThrows(Exception.class, () -> {
            new RSAServiceImpl("invalidPublicKey", "invalidPrivateKey");
        });
    }
}