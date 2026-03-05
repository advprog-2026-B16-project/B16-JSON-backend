package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceImplTest {

    private StubUserRepository stubUserRepository;
    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        stubUserRepository = new StubUserRepository();
        registrationService = new RegistrationServiceImpl(stubUserRepository);
    }

    @Test
    void testRegisterSavesUserWithCorrectAttributes() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        registrationService.register(request);

        List<User> users = stubUserRepository.findAll();
        assertEquals(1, users.size());
        
        User savedUser = users.get(0);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("password123", savedUser.getPassword());
        assertEquals(UserRole.TITIPER, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }
}
