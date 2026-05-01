package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;
    
    @Mock
    private OrderService orderService;

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

    @Test
    void testUpdateProfile() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("john");
        when(userService.getUserByUsername("john")).thenReturn(Optional.of(user1));
        
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("John Doe")
                .build();
        
        ResponseEntity<?> response = userController.updateProfile(request, principal);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateProfile(user1.getId(), request);
    }

    @Test
    void testGetProfile() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("john");
        when(userService.getUserByUsername("john")).thenReturn(Optional.of(user1));
        
        ResponseEntity<UserProfileResponse> response = userController.getProfile(principal);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john", response.getBody().getUsername());
    }

    @Test
    void testGetPublicProfileJastiper() {
        when(userService.getUserByUsername("jane")).thenReturn(Optional.of(user2));
        when(orderService.getOrderByJastiperId(user2.getId().toString())).thenReturn(new ArrayList<>());
        
        ResponseEntity<UserProfileResponse> response = userController.getPublicProfile("jane");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jane", response.getBody().getUsername());
        assertEquals(0L, response.getBody().getSuccessfulTransactions());
    }
}
