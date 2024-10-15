package main.BankApp.User.Service.User;

import lombok.RequiredArgsConstructor;
import main.BankApp.User.ENTITY.StatusEnum;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.Repository.UserRepository;
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
        userAccount.setStatus(StatusEnum.LOCKED);
        userRepository.save(userAccount);
        logger.info("Account for user ID: {} has been locked", userAccount.getUserId());
    }
}
