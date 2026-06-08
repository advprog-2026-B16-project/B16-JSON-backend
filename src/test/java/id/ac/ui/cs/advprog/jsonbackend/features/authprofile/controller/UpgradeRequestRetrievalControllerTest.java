package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpgradeRequestRetrievalControllerTest {

    private UpgradeRequestRetrievalService retrievalService;
    private UserService userService;
    private UpgradeRequestRetrievalController controller;
    private User user;
    private UpgradeRequest request;

    @BeforeEach
    void setUp() {
        retrievalService = mock(UpgradeRequestRetrievalService.class);
        userService = mock(UserService.class);
        controller = new UpgradeRequestRetrievalController(retrievalService, userService);

        user = User.builder()
                .id(UUID.randomUUID())
                .username("titiper")
                .email("titiper@example.com")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();

        request = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .requesterUser(user)
                .fullName("Titiper User")
                .credential("credential")
                .socialMediaUrl("https://example.com/titiper")
                .status("PENDING")
                .build();
    }

    @Test
    void getAllRequestsShouldReturnMappedResponses() {
        when(retrievalService.getAllRequests()).thenReturn(List.of(request));

        ResponseEntity<?> response = controller.getAllRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, ((List<?>) response.getBody()).size());
        verify(retrievalService).getAllRequests();
    }

    @Test
    void getAllRequestsShouldSupportVerboseLoggingBranch() {
        ReflectionTestUtils.setField(controller, "verboseLogging", true);
        when(retrievalService.getAllRequests()).thenReturn(List.of(request));

        ResponseEntity<?> response = controller.getAllRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(retrievalService).getAllRequests();
    }

    @Test
    void getMyRequestShouldReturnUnauthorizedWhenAuthenticationMissing() {
        ResponseEntity<?> response = controller.getMyRequest(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userService, never()).getUserByUsername("titiper");
    }

    @Test
    void getMyRequestShouldReturnMappedResponseWhenRequestExists() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("titiper");
        when(userService.getUserByUsername("titiper")).thenReturn(Optional.of(user));
        when(retrievalService.getRequestByUsername(user)).thenReturn(Optional.of(request));

        ResponseEntity<?> response = controller.getMyRequest(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).getUserByUsername("titiper");
        verify(retrievalService).getRequestByUsername(user);
    }

    @Test
    void getMyRequestShouldReturnNoContentWhenUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("missing");
        when(userService.getUserByUsername("missing")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getMyRequest(authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(retrievalService, never()).getRequestByUsername(user);
    }

    @Test
    void getMyRequestShouldReturnNoContentWhenRequestNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("titiper");
        when(userService.getUserByUsername("titiper")).thenReturn(Optional.of(user));
        when(retrievalService.getRequestByUsername(user)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getMyRequest(authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(retrievalService).getRequestByUsername(user);
    }
}
