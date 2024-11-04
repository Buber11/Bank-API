package main.BankApp.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserModel;
import main.BankApp.expection.RSAException;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.model.user.UserPersonalData;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.hashing.HashingService;
import main.BankApp.service.rsa.RSAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RSAService rsaService;
    private final HashingService hashingService;
    private final PasswordEncoder passwordEncoder;
    private final UserModelAssembly userModelAssembly;

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
    public UserModel getUserView(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        UserAccount userAccount = userRepository.findById(userId)
                .orElseThrow( EntityNotFoundException::new );
        return userModelAssembly.toModel(userAccount);
    }

    @Override
    public List<UserModel> getUsersView(StatusAccount statusAccount) {
        List<UserAccount> accounts = userRepository.findByStatus(statusAccount);
        return accounts.stream()
                .map(userModelAssembly::toModel)
                .toList();
    }

    @Override
    public List<UserModel> getAllUsers() {
        List<UserAccount> accounts = userRepository.findAll();
        return accounts.stream()
                .map(userModelAssembly::toModel)
                .toList();
    }

    @Override
    public void changeUserStatus(long userId, StatusAccount statusAccount) {
        UserAccount userAccount = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
        userAccount.setStatus(statusAccount);
        userRepository.save(userAccount);
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

}
