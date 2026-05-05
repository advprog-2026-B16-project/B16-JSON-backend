package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UpgradeRequestStatusChangeServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UpgradeRequestRepository upgradeRepo;

    private UpgradeRequestStatusChangeServiceImpl service;
    private User testUser;
    private UUID requestId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UpgradeRequestStatusChangeServiceImpl(userService, userRepository, upgradeRepo);

        requestId = UUID.randomUUID();
        testUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .role(UserRole.TITIPER)
            .build();
    }

    @Test
    void testSubmitUpgradeRequest_Success_NoExistingRequest() {
        UpgradeRequestSubmissionRequest request = new UpgradeRequestSubmissionRequest();
        request.setFullName("Test User");
        request.setCredential("credential_url");
        request.setSocialMediaUrl("social_media_url");

        UpgradeRequest savedRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .requesterUser(testUser)
            .fullName("Test User")
            .credential("credential_url")
            .socialMediaUrl("social_media_url")
            .status("PENDING")
            .build();

        when(upgradeRepo.findByRequesterUser(testUser)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(upgradeRepo.save(any(UpgradeRequest.class))).thenReturn(savedRequest);

        UpgradeRequestResponse response = service.submitUpgradeRequest(testUser, request);

        assertNotNull(response);
        assertEquals(requestId, response.id());
        assertEquals("Test User", response.fullName());
        verify(upgradeRepo, times(1)).findByRequesterUser(testUser);
        verify(userRepository, times(1)).save(testUser);
        verify(upgradeRepo, times(1)).save(any(UpgradeRequest.class));
    }

    @Test
    void testSubmitUpgradeRequest_ExistingPendingRequest() {
        UpgradeRequestSubmissionRequest request = new UpgradeRequestSubmissionRequest();
        request.setFullName("Test User");

        UpgradeRequest existingRequest = UpgradeRequest.builder()
            .upgrReqId(UUID.randomUUID())
            .requesterUser(testUser)
            .status("PENDING")
            .build();

        when(upgradeRepo.findByRequesterUser(testUser)).thenReturn(Optional.of(existingRequest));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.submitUpgradeRequest(testUser, request);
        });

        assertEquals("Pending request exists", exception.getMessage());
        verify(upgradeRepo, times(1)).findByRequesterUser(testUser);
        verify(upgradeRepo, never()).delete(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSubmitUpgradeRequest_ExistingApprovedRequest() {
        UpgradeRequestSubmissionRequest request = new UpgradeRequestSubmissionRequest();
        request.setFullName("Test User");

        UpgradeRequest existingRequest = UpgradeRequest.builder()
            .upgrReqId(UUID.randomUUID())
            .requesterUser(testUser)
            .status("ACCEPTED")
            .build();

        UpgradeRequest newRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .requesterUser(testUser)
            .fullName("Test User")
            .status("PENDING")
            .build();

        when(upgradeRepo.findByRequesterUser(testUser)).thenReturn(Optional.of(existingRequest));
        when(upgradeRepo.save(any(UpgradeRequest.class))).thenReturn(newRequest);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UpgradeRequestResponse response = service.submitUpgradeRequest(testUser, request);

        assertNotNull(response);
        verify(upgradeRepo, times(1)).delete(existingRequest);
        verify(upgradeRepo, times(1)).flush();
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateRequestStatus_AcceptRequest() {
        UpgradeRequest upgradeRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .requesterUser(testUser)
            .status("PENDING")
            .build();

        when(upgradeRepo.findById(requestId)).thenReturn(Optional.of(upgradeRequest));

        service.updateRequestStatus(requestId, "ACCEPTED");

        assertEquals("ACCEPTED", upgradeRequest.getStatus());
        assertEquals(UserStatus.ACTIVE, testUser.getStatus());
        verify(userService, times(1)).promoteToJastiper(testUser);
        verify(userRepository, times(1)).save(testUser);
        verify(upgradeRepo, times(1)).save(upgradeRequest);
    }

    @Test
    void testUpdateRequestStatus_RejectRequest() {
        UpgradeRequest upgradeRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .requesterUser(testUser)
            .status("PENDING")
            .build();

        when(upgradeRepo.findById(requestId)).thenReturn(Optional.of(upgradeRequest));

        service.updateRequestStatus(requestId, "REJECTED");

        assertEquals("REJECTED", upgradeRequest.getStatus());
        assertEquals(UserStatus.ACTIVE, testUser.getStatus());
        verify(userService, never()).promoteToJastiper(testUser);
        verify(userRepository, times(1)).save(testUser);
        verify(upgradeRepo, times(1)).save(upgradeRequest);
    }

    @Test
    void testUpdateRequestStatus_NotFound() {
        when(upgradeRepo.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateRequestStatus(requestId, "ACCEPTED");
        });

        assertEquals("Not found", exception.getMessage());
        verify(userService, never()).promoteToJastiper(any());
    }

    @Test
    void testUpdateRequestStatus_CaseInsensitiveAccepted() {
        UpgradeRequest upgradeRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .requesterUser(testUser)
            .status("PENDING")
            .build();

        when(upgradeRepo.findById(requestId)).thenReturn(Optional.of(upgradeRequest));

        service.updateRequestStatus(requestId, "accepted");

        assertEquals("accepted", upgradeRequest.getStatus());
        verify(userService, times(1)).promoteToJastiper(testUser);
    }
}
