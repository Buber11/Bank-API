package main.BankApp.Session.Session;

import main.BankApp.Session.enitity.ActivityLog;
import main.BankApp.Session.enitity.ActivityLogAction;
import main.BankApp.Session.enitity.Session;

public interface ActivityLogService {
    ActivityLog createLog(Session session, ActivityLogAction action);

    ActivityLog getLog(Long logId);

    void deleteLog(Long logId);
}
