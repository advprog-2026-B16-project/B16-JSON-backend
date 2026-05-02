package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.Authentication;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
class UpgradeControllerTest {
    @Mock private UpgradeRequestStatusChangeService upgradeService;
    @Mock private UserService userService;
    @Mock private BindingResult bindingResult;
    @Mock private Authentication authentication;
    @InjectMocks private UpgradeRequestStatusChangeController controller;
    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }
    @Test void testSubmit() {
        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        when(authentication.getName()).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(Optional.of(new User()));
        controller.submit(dto, authentication);
        verify(upgradeService).submitUpgradeRequest(any(), any());
    }
    @Test void testUpdate() {
        UUID id = UUID.randomUUID();
        UpgradeRequestStatusChangeRequest dto = new UpgradeRequestStatusChangeRequest();
        controller.update(id, dto);
        verify(upgradeService).updateRequestStatus(any(), any());
    }
}