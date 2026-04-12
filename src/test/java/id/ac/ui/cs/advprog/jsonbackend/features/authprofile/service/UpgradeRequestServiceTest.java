package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UpgradeRequestStatusChangeServiceImpl statusChangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateStatusAccepted() {
        UUID requestId = UUID.randomUUID();
        User user = User.builder().username("testuser").role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(requestId)
                .requesterUser(user)
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId, "ACCEPTED");

        assertEquals("ACCEPTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userService).promoteToJastiper(user);
    }

    @Test
    void testSubmitUpgradeRequestSuccess() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest dto = 
            new id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest();
        dto.setFullName("Test User");
        dto.setCredential("Credential");

        UpgradeRequest savedRequest = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(user)
                .fullName(dto.getFullName())
                .credential(dto.getCredential())
                .status("PENDING")
                .createdAt(java.time.OffsetDateTime.now())
                .build();

        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.empty());
        when(upgradeRequestRepository.save(any())).thenReturn(savedRequest);

        statusChangeService.submitUpgradeRequest(user, dto);

        verify(upgradeRequestRepository).save(any());
    }

    @Test
    void testSubmitUpgradeRequestAlreadyPending() {
        User user = new User();
        user.setUsername("testuser");
        id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest dto = 
            new id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest();

        UpgradeRequest existing = UpgradeRequest.builder().status("PENDING").build();
        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> statusChangeService.submitUpgradeRequest(user, dto));
    }

    @Test
    void testUpdateStatusRejected() {
        UUID requestId = UUID.randomUUID();
        User user = User.builder().role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(requestId)
                .requesterUser(user)
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId, "REJECTED");

        assertEquals(UserRole.TITIPER, user.getRole());
        assertEquals("REJECTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testUpdateStatusNotFound() {
        UUID requestId = UUID.randomUUID();
        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> statusChangeService.updateRequestStatus(requestId, "ACCEPTED"));
    }

    @Test
    void testVerboseLogging() throws Exception {
        // Use reflection to set private field
        java.lang.reflect.Field field = UpgradeRequestStatusChangeServiceImpl.class.getDeclaredField("verboseLogging");
        field.setAccessible(true);
        field.set(statusChangeService, true);

        UUID requestId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).username("testuser").role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(requestId)
                .requesterUser(user)
                .fullName("Full Name")
                .credential("Cred")
                .status("PENDING")
                .createdAt(java.time.OffsetDateTime.now())
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId, "ACCEPTED");

        assertEquals("ACCEPTED", request.getStatus());
        
        // Test submit with verbose logging
        id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest dto = 
            new id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest();
        dto.setFullName("Test User");
        dto.setCredential("Credential");
        
        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.empty());
        when(upgradeRequestRepository.save(any())).thenReturn(request);
        
        statusChangeService.submitUpgradeRequest(user, dto);
        
        verify(upgradeRequestRepository, atLeastOnce()).save(any());
    }

    @Test
    void testSubmitUpgradeRequestAcceptedExists() {
        User user = User.builder().id(UUID.randomUUID()).username("testuser").build();
        id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest dto = 
            new id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest();
        dto.setFullName("Full Name");
        dto.setCredential("Cred");

        // If an ACCEPTED request exists, it should ALLOW a new one (e.g. if they were demoted later)
        UpgradeRequest existing = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(user)
                .status("ACCEPTED")
                .fullName("Old")
                .credential("Old")
                .createdAt(java.time.OffsetDateTime.now())
                .build();
        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.of(existing));
        when(upgradeRequestRepository.save(any())).thenReturn(existing);

        statusChangeService.submitUpgradeRequest(user, dto);
        verify(upgradeRequestRepository).save(any());
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
