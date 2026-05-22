package id.ac.ui.cs.advprog.jsonbackend.common.config;

import org.junit.jupiter.api.Test;

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
}
