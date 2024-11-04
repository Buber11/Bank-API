package main.BankApp.service.googleAuthenticator;

public interface GoogleAuthService {

    String generateSecretKey();

    String getTOTPCode(String secretKey);

    String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer);

}
