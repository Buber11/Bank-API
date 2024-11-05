package main.BankApp.service.googleAuthenticator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.taimos.totp.TOTP;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final SecureRandom random;
    private final Base32 base32;

    @Override
    public String generateSecretKey() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return base32.encodeToString(bytes);
    }

    @Override
    public String getTOTPCode(String secretKey) {
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    @Override
    public String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return buildBarCodeUri(secretKey, account, issuer);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error encoding Google Authenticator URI", e);
        }
    }

    private String buildBarCodeUri(String secretKey, String account, String issuer) throws UnsupportedEncodingException {
        String encodedAccountInfo = encodeValue(issuer + ":" + account);
        String encodedSecret = encodeValue(secretKey);
        String encodedIssuer = encodeValue(issuer);

        return String.format("otpauth://totp/%s?secret=%s&issuer=%s",
                encodedAccountInfo, encodedSecret, encodedIssuer);
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
    }
}
