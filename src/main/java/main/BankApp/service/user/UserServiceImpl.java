package main.BankApp.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public void lockAccount(UserAccount userAccount) {
        logger.info("Locking account for user ID: {}", userAccount.getUserId());
        userAccount.setStatus(StatusAccount.LOCKED);
        userRepository.save(userAccount);
        logger.info("Account for user ID: {} has been locked", userAccount.getUserId());
    }

    @Override
    public UserAccount get(long id) {
        return userRepository.findById(id)
                .orElseThrow( EntityNotFoundException::new );
    }

}
