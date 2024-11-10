package main.BankApp.service.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserModel;
import main.BankApp.expection.UserNotFoundException;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.hashing.HashingService;
import main.BankApp.service.rsa.VaultService;
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
    private final VaultService vaultService;
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
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    @Override
    public UserModel getUserView(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        UserAccount userAccount = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
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
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userAccount.setStatus(statusAccount);
        userRepository.save(userAccount);
        logger.info("Changed status for user ID: {} to {}", userId, statusAccount);
    }

    @Override
    public void delete(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        logger.info("Deleted user with ID: {}", userId);
    }
}
