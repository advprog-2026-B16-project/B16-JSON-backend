package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .email("john@example.com")
                .password("pass1")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        user2 = User.builder()
                .id(UUID.randomUUID())
                .username("jane")
                .email("jane@example.com")
                .password("pass2")
                .role(UserRole.JASTIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void testGetUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<?> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertEquals(2, body.size());
    }

    @Test
    void testBanUser() {
        UUID id = UUID.randomUUID();
        ResponseEntity<?> response = userController.banUser(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).banUser(id);
    }

    @Test
    void testDemoteUser() {
        UUID id = UUID.randomUUID();
        ResponseEntity<?> response = userController.demoteUser(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).demoteUser(id);
    }
}
