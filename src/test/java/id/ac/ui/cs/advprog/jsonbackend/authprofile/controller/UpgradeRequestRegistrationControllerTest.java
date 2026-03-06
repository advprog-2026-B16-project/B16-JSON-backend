package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UpgradeRequestRegistrationControllerTest {

    @Mock
    private UpgradeRequestService upgradeRequestService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UpgradeRequestRegistrationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyForUpgradeSuccess() {
        UpgradeRequestRegistrationRequest request = new UpgradeRequestRegistrationRequest();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = User.builder().username("testuser").build();
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.applyForUpgrade(request, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(upgradeRequestService).createRequest(eq(user), eq(request));
    }

    @Test
    void testApplyForUpgradeBindingErrors() {
        UpgradeRequestRegistrationRequest request = new UpgradeRequestRegistrationRequest();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = controller.applyForUpgrade(request, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testApplyForUpgradeUserNotFound() {
        UpgradeRequestRegistrationRequest request = new UpgradeRequestRegistrationRequest();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("unknown");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getUserByUsername("unknown")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.applyForUpgrade(request, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
