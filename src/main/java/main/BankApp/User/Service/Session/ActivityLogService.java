package main.BankApp.User.Service.Session;

import main.BankApp.User.ENTITY.ActivityLog;
import main.BankApp.User.ENTITY.ActivityLogAction;
import main.BankApp.User.ENTITY.Session;
import main.BankApp.User.ENTITY.UserAccount;

import java.util.List;

public interface ActivityLogService {
    ActivityLog createLog(Session session, ActivityLogAction action);

    ActivityLog getLog(Long logId);

    void deleteLog(Long logId);
}
