package main.BankApp.service.activityLog;

import main.BankApp.model.session.ActivityLog;
import main.BankApp.model.session.ActivityLogAction;
import main.BankApp.model.session.Session;

public interface ActivityLogService {
    ActivityLog createLog(Session session, ActivityLogAction action);

    ActivityLog getLog(String logId);

    void deleteLog(String logId);
}
