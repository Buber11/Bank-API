package main.BankApp;

import static org.junit.jupiter.api.Assertions.*;

import main.BankApp.service.hashing.HashingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashingServiceImplTest {

    private HashingServiceImpl hashingService;

    @BeforeEach
    public void setUp() {
        hashingService = new HashingServiceImpl();
    }

    @Test
    public void testHash() {
        String rawData = "mySecurePassword";

        String hashedData = hashingService.hash(rawData);


        assertNotNull(hashedData);
        assertNotEquals(rawData, hashedData);

        assertTrue(hashingService.matches(rawData, hashedData));
    }

    @Test
    public void testMatchesWithCorrectPassword() {
        String rawData = "mySecurePassword";
        String hashedData = hashingService.hash(rawData);


        assertTrue(hashingService.matches(rawData, hashedData));
    }

    @Test
    public void testMatchesWithIncorrectPassword() {
        String rawData = "mySecurePassword";
        String hashedData = hashingService.hash(rawData);
        String incorrectData = "wrongPassword";


        assertFalse(hashingService.matches(incorrectData, hashedData));
    }

    @Test
    public void testHashingUniqueResults() {
        String rawData = "mySecurePassword";


        String hashedData1 = hashingService.hash(rawData);
        String hashedData2 = hashingService.hash(rawData);


        assertNotEquals(hashedData1, hashedData2);
    }
}
