package app.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;


@Component
public class Session {
    
    private Map<String, HttpSession> activeSessions = new ConcurrentHashMap<>();

    public void addSession(HttpSession session) {
        activeSessions.put(session.getId(), session);
    }

    public void removeSession(HttpSession session) {
        activeSessions.remove(session.getId());
    }

    public boolean isSessionActive(HttpSession session) {
        return activeSessions.containsKey(session.getId());
    }
    
    public void setSessionTimeout(HttpSession session, int minutes) {
        session.setMaxInactiveInterval(minutes * 60);
    }
}
