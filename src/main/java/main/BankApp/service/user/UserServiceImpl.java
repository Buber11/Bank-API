package main.BankApp.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.common.Loggable;
import main.BankApp.dto.UserDataView;
import main.BankApp.expection.RSAException;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.model.user.UserPersonalData;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.rsa.RSAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RSAService rsaService;

    @Override
    public void lockAccount(UserAccount userAccount) {
        logger.info("Locking account for user ID: {}", userAccount.getUserId());
        userAccount.setStatus(StatusAccount.LOCKED);
        userRepository.save(userAccount);
        logger.info("Account for user ID: {} has been locked", userAccount.getUserId());
    }

    @Override
    public UserAccount getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow( EntityNotFoundException::new );
    }

    @Override
    @Loggable
    public UserDataView getUserView(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        UserAccount userAccount = userRepository.findById(userId)
                .orElseThrow( EntityNotFoundException::new );
        UserPersonalData personalData = userAccount.getUserPersonalData();

        UserDataView view = null;
        try {
            view = UserDataView.builder()
                    .username( userAccount.getUsername() )
                    .email(rsaService.decrypt(userAccount.getEmail()))
                    .status( userAccount.getStatus() )
                    .lastLogin( userAccount.getLastLogin() )
                    .isBusinessAccount( userAccount.isBusinessAccount() )
                    .twoFactorEnabled( userAccount.isTwoFactorEnabled() )
                    .consentToCommunication( userAccount.isConsentToCommunication() )
                    .firstName( rsaService.decrypt(personalData.getFirstName()) )
                    .lastName( rsaService.decrypt(personalData.getLastName()) )
                    .countryOfOrigin( rsaService.decrypt(personalData.getCountryOfOrigin()) )
                    .phoneNumber( rsaService.decrypt(personalData.getPhoneNumber()) )
                    .pesel( rsaService.decrypt(personalData.getPesel()) )
                    .build();
        } catch (Exception e) {
            throw new RSAException(e.getMessage());
        }

        return view;
    }

}
