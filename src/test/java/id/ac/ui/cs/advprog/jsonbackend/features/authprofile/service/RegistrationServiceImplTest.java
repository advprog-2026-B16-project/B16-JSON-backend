package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegistrationServiceImpl registrationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationServiceImpl(userRepository, passwordEncoder, applicationEventPublisher);
    }

    @Test
    void testRegisterSuccessWithId() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        java.util.UUID userId = java.util.UUID.randomUUID();
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        
        // Mock save to return user with ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userId);
            return u;
        });

        registrationService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(applicationEventPublisher).publishEvent(any(id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent.class));
    }

    @Test
    void testRegisterSuccessNullId() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser2");
        request.setEmail("new2@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername("newuser2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new2@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        
        // save returns user without ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registrationService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(applicationEventPublisher).publishEvent(any(id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent.class));
    }

    @Test
    void testRegisterDuplicateUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existinguser");

        User existingUser = User.builder().build();
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> registrationService.register(request));
    }

    @Test
    void testRegisterDuplicateEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(User.builder().build()));

        assertThrows(RuntimeException.class, () -> registrationService.register(request));
    }
}
