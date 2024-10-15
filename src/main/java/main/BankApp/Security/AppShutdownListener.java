package main.BankApp.Security;

import lombok.RequiredArgsConstructor;

import main.BankApp.Session.Session.SessionService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private final SessionService sessionService;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        sessionService.invalidateAllActiveSessions();
    }
}
