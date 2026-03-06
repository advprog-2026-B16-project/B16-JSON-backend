package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.config.SanitizationService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UpgradeRequestServiceTest {

    @Mock
    private UpgradeRequestRepository upgradeRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private SanitizationService sanitizationService;

    private UpgradeRequestStatusChangeServiceImpl statusChangeService;
    private UpgradeRequestServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statusChangeService = new UpgradeRequestStatusChangeServiceImpl(upgradeRequestRepository, userService);
        registrationService = new UpgradeRequestServiceImpl(upgradeRequestRepository, sanitizationService);
    }

    @Test
    void testUpdateStatusAccepted() {
        UUID requestId = UUID.randomUUID();
        User user = User.builder().role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .id(requestId)
                .requesterUser(user)
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId.toString(), "ACCEPTED");

        assertEquals("ACCEPTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userService).promoteToJastiper(user);
    }

    @Test
    void testUpdateStatusRejected() {
        UUID requestId = UUID.randomUUID();
        User user = User.builder().role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .id(requestId)
                .requesterUser(user)
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId.toString(), "REJECTED");

        assertEquals("REJECTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userService, never()).promoteToJastiper(any());
    }

    @Test
    void testUpdateStatusNotFound() {
        UUID requestId = UUID.randomUUID();
        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> statusChangeService.updateRequestStatus(requestId.toString(), "ACCEPTED"));
    }

    @Test
    void testCreateRequestSuccess() {
        User user = User.builder().username("testuser").build();
        UpgradeRequestRegistrationRequest dto = new UpgradeRequestRegistrationRequest();
        dto.setFullName("John Doe");
        dto.setCredential("Passport");

        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.empty());
        when(sanitizationService.sanitize("John Doe")).thenReturn("John Doe");
        when(sanitizationService.sanitize("Passport")).thenReturn("Passport");

        registrationService.createRequest(user, dto);

        verify(upgradeRequestRepository).save(any(UpgradeRequest.class));
    }

    @Test
    void testCreateRequestDuplicate() {
        User user = User.builder().username("testuser").build();
        UpgradeRequestRegistrationRequest dto = new UpgradeRequestRegistrationRequest();

        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.of(new UpgradeRequest()));

        assertThrows(RuntimeException.class, () -> registrationService.createRequest(user, dto));
    }

    @Test
    void testGetAllRequests() {
        UpgradeRequestRetrievalServiceImpl retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository);
        retrievalService.getAllRequests();
        verify(upgradeRequestRepository).findAll();
    }

    @Test
    void testGetRequestByUsername() {
        UpgradeRequestRetrievalServiceImpl retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository);
        User user = mock(User.class);
        retrievalService.getRequestByUsername(user);
        verify(upgradeRequestRepository).findByRequesterUser(user);
    }
}
