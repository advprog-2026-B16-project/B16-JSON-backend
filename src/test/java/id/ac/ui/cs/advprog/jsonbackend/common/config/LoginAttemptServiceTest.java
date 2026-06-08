package id.ac.ui.cs.advprog.jsonbackend.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    @Test
    void loginFailedShouldBlockAfterFiveAttemptsAndSuccessShouldReset() {
        LoginAttemptService service = new LoginAttemptService();
        String key = "titiper";

        for (int i = 0; i < 4; i++) {
            service.loginFailed(key);
            assertFalse(service.isBlocked(key));
        }

        service.loginFailed(key);
        assertTrue(service.isBlocked(key));

        service.loginSucceeded(key);
        assertFalse(service.isBlocked(key));
    }

    @Test
    @SuppressWarnings("unchecked")
    void isBlockedShouldReleaseExpiredLock() {
        LoginAttemptService service = new LoginAttemptService();
        String key = "expired-user";

        for (int i = 0; i < 5; i++) {
            service.loginFailed(key);
        }

        Map<String, Long> lockTimeCache =
                (Map<String, Long>) ReflectionTestUtils.getField(service, "lockTimeCache");
        lockTimeCache.put(key, System.currentTimeMillis() - 301_000);

        assertFalse(service.isBlocked(key));
        assertFalse(service.isBlocked(key));
    }
}
