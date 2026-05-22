package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminUserInitializerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AdminUserInitializer initializer;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        initializer = new AdminUserInitializer(userRepository, passwordEncoder);
    }

    @Test
    void runSkipsWhenCredentialsAreMissing() {
        configure("", "admin", "");

        initializer.run(mock(ApplicationArguments.class));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void runCreatesAdminWhenEmailDoesNotExist() {
        configure("admin.jsonbackend@gmail.com", "admin", "StrongPass123!");

        when(userRepository.findByEmail("admin.jsonbackend@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("StrongPass123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        initializer.run(mock(ApplicationArguments.class));

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("admin.jsonbackend@gmail.com")
                        && user.getUsername().equals("admin")
                        && user.getPassword().equals("encoded-password")
                        && user.getRole() == UserRole.ADMIN
                        && user.getStatus() == UserStatus.ACTIVE
        ));
    }

    @Test
    void runPromotesExistingUserToAdmin() {
        configure("admin.jsonbackend@gmail.com", "admin", "StrongPass123!");
        User existingUser = User.builder()
                .username("olduser")
                .email("admin.jsonbackend@gmail.com")
                .password("old-password")
                .role(UserRole.TITIPER)
                .status(UserStatus.BANNED)
                .build();

        when(userRepository.findByEmail("admin.jsonbackend@gmail.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        initializer.run(mock(ApplicationArguments.class));

        assertEquals(UserRole.ADMIN, existingUser.getRole());
        assertEquals(UserStatus.ACTIVE, existingUser.getStatus());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existingUser);
    }

    private void configure(String email, String username, String password) {
        ReflectionTestUtils.setField(initializer, "adminEmail", email);
        ReflectionTestUtils.setField(initializer, "adminUsername", username);
        ReflectionTestUtils.setField(initializer, "adminPassword", password);
    }
}
