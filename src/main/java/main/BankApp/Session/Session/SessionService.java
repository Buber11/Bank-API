package main.BankApp.Session.Session;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.Session.enitity.Session;
import main.BankApp.User.ENTITY.UserAccount;

public interface SessionService {

    Session createSession(UserAccount userAccount, String ipAddress, String userAgent);


    String getClientIp(HttpServletRequest request);
    String getUserAgent(HttpServletRequest request);

    void invalidateSession(String sessionId);

    boolean isSessionActive(String sessionId);


    void invalidateExpiredSessions();

    boolean checkSession(long userId, String ip, String userAgent);

    void invalidateAllActiveSessions();

    String getSessionId(long userId);

    void invalidateSession(long userId);



}
