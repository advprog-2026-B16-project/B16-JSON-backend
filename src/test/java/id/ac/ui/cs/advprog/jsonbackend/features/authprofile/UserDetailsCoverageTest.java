package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class UserDetailsCoverageTest {

    @Test
    void testUserDetailsMethods() {
        User user = User.builder()
                .username("tester")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertEquals("tester", user.getUsername());
        assertEquals(Collections.singletonList(new SimpleGrantedAuthority("ROLE_TITIPER")), user.getAuthorities());

        user.setStatus(UserStatus.BANNED);
        assertFalse(user.isAccountNonLocked());
    }
}
