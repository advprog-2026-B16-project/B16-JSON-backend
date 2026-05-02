package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthProfileFinalAuditTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_AutoUsernameGeneration() {
        UserRegistrationRequest dto = new UserRegistrationRequest();
        dto.setEmail("test.user@gmail.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");
        // username is null

        when(userRepository.findByUsername("test.user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        registrationService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        assertEquals("test.user", userCaptor.getValue().getUsername());
        assertEquals("test.user@gmail.com", userCaptor.getValue().getEmail());
    }

    @Test
    void testRegister_AutoUsernameGeneration_Blank() {
        UserRegistrationRequest dto = new UserRegistrationRequest();
        dto.setUsername("  ");
        dto.setEmail("blank.user@gmail.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");

        when(userRepository.findByUsername("blank.user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        registrationService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        assertEquals("blank.user", userCaptor.getValue().getUsername());
    }
}
