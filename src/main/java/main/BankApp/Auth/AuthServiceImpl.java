package main.BankApp.Auth;

import main.BankApp.SecurityAlgorithms.Hash.HashingService;
import main.BankApp.SecurityAlgorithms.RSA.RSAService;
import main.BankApp.User.ENTITY.StatusEnum;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.ENTITY.UserPersonalData;
import main.BankApp.User.Repository.UserPersonalDataRepository;
import main.BankApp.User.Repository.UserRepository;
import main.BankApp.User.Request.SignupRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    UserRepository userRepository;
    UserPersonalDataRepository UserPersonalDataRepository;
    RSAService rsaService;
    HashingService hashingService;

    PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, main.BankApp.User.Repository.UserPersonalDataRepository userPersonalDataRepository, RSAService rsaService, HashingService hashingService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        UserPersonalDataRepository = userPersonalDataRepository;
        this.rsaService = rsaService;
        this.hashingService = hashingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signup(SignupRequest request) throws Exception {

        List<String> hashedPeselList = UserPersonalDataRepository.findAllPeselHash();
        for (String hashedPesel : hashedPeselList){
            if(hashingService.matches(request.pesel(),hashedPesel)){
                throw new RuntimeException("This Pesel has already existed");
            }
        }

        UserAccount newUserAccount = UserAccount.builder()
                .username(request.username())
                .email(rsaService.encrypt(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(StatusEnum.PENDING)
                .hmac(hashingService.hash( request.username() + request.email() + StatusEnum.PENDING + "0" + "False" + "False"))
                .build();

        UserPersonalData newUserPersonalData = UserPersonalData.builder()
                .firstName(rsaService.encrypt(request.firstName()))
                .lastName(rsaService.encrypt(request.lastName()))
                .pesel(rsaService.encrypt(request.pesel()))
                .peselHash(hashingService.hash(request.pesel()))
                .idCardNumber(rsaService.encrypt("0"))
                .phoneNumber(request.phoneNumber())
                .countryOfOrigin(request.countryOfOrigin())
                .hmac(hashingService.hash(request.firstName() + request.lastName() + request.pesel() + request.phoneNumber() + request.countryOfOrigin()))
                .build();

        newUserAccount.setUserPersonalData(newUserPersonalData);
        newUserPersonalData.setUserAccount(newUserAccount);

        userRepository.save(newUserAccount);
    }
}
