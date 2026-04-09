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
        User user = User.builder().role(UserRole.TITIPER).build();
        UpgradeRequest request = UpgradeRequest.builder()
                .id(requestId)
                .requesterUser(user)
                .build();

        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        statusChangeService.updateRequestStatus(requestId, "ACCEPTED");

        assertEquals(UserRole.JASTIPER, user.getRole());
        assertEquals("ACCEPTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userRepository).save(user);
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
