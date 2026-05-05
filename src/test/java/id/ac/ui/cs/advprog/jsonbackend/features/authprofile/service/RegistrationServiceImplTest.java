package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationServiceImpl(userRepository, passwordEncoder, eventPublisher);
    }

    @Test
    void testRegister_Success_WithProvidedUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void testRegister_Success_AutoGenerateUsername_FromEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(null);
        request.setEmail("testuser@example.com");
        request.setPassword("password123");

        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("testuser@example.com")
            .password("encodedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        assertEquals("testuser", request.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void testRegister_Success_AutoGenerateUsername_BlankUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("   ");
        request.setEmail("blanktest@example.com");
        request.setPassword("password123");

        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .username("blanktest")
            .email("blanktest@example.com")
            .password("encodedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findByUsername("blanktest")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("blanktest@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        assertEquals("blanktest", request.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User existingUser = User.builder()
            .id(UUID.randomUUID())
            .username("existinguser")
            .build();

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        User existingUser = User.builder()
            .id(UUID.randomUUID())
            .email("existing@example.com")
            .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testRegister_PublishesUserCreatedEvent() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("eventtest");
        request.setEmail("event@example.com");
        request.setPassword("password123");

        UUID userId = UUID.randomUUID();
        User savedUser = User.builder()
            .id(userId)
            .username("eventtest")
            .email("event@example.com")
            .password("encodedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findByUsername("eventtest")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("event@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void testRegister_UserCreatedEvent_WhenUserIdIsNull() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("nullidtest");
        request.setEmail("nullid@example.com");
        request.setPassword("password123");

        User savedUser = User.builder()
            .id(null)
            .username("nullidtest")
            .email("nullid@example.com")
            .password("encodedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findByUsername("nullidtest")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nullid@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registrationService.register(request);

        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedEvent.class));
    }
}
