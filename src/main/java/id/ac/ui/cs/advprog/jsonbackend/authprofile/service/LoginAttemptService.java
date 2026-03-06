package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 5;
    private static final int BLOCK_DURATION_MINUTES = 5;
    
    private final Map<String, CachedValue> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        CachedValue cachedValue = attemptsCache.get(key);
        if (cachedValue == null) {
            cachedValue = new CachedValue(0, LocalDateTime.now());
        }
        cachedValue.attempts++;
        cachedValue.lastAttempt = LocalDateTime.now();
        attemptsCache.put(key, cachedValue);
    }

    public boolean isBlocked(String key) {
        CachedValue cachedValue = attemptsCache.get(key);
        if (cachedValue == null) {
            return false;
        }
        
        if (cachedValue.attempts >= MAX_ATTEMPT) {
            if (cachedValue.lastAttempt.plusMinutes(BLOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                return true;
            } else {
                // Block expired
                attemptsCache.remove(key);
                return false;
            }
        }
        return false;
    }

    private static class CachedValue {
        int attempts;
        LocalDateTime lastAttempt;

        CachedValue(int attempts, LocalDateTime lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }
    }
}
