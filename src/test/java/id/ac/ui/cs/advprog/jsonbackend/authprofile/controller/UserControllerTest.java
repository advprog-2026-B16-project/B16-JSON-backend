package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        user1 = new User("john", "john@example.com", "pass1", UserRole.TITIPER, UserStatus.ACTIVE);
        user2 = new User("jane", "jane@example.com", "pass2", UserRole.JASTIPER, UserStatus.ACTIVE);
    }

    @Test
    void testUserControllerNotNull() {
        assertNotNull(userController);
    }

    @Test
    void testGetUsersReturnsOk() {
        when(userService.getAllUsers()).thenReturn(List.of());
        ResponseEntity<?> response = userController.getUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUsersReturnsAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
        ResponseEntity<?> response = userController.getUsers();
        List<User> users = (List<User>) response.getBody();
        assertEquals(2, users.size());
        assertEquals("john", users.get(0).getUsername());
        assertEquals("jane", users.get(1).getUsername());
    }
}
