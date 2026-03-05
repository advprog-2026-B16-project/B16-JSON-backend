package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
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
    private UserRepository userRepository;

    private UpgradeRequestStatusChangeServiceImpl statusChangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statusChangeService = new UpgradeRequestStatusChangeServiceImpl(upgradeRequestRepository, userRepository);
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

        statusChangeService.updateRequestStatus(requestId.toString(), "REJECTED");

        assertEquals(UserRole.TITIPER, user.getRole());
        assertEquals("REJECTED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testUpdateStatusNotFound() {
        UUID requestId = UUID.randomUUID();
        when(upgradeRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> statusChangeService.updateRequestStatus(requestId.toString(), "ACCEPTED"));
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
