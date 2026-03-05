package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpgradeRequestServiceTest {

    @Mock
    private UpgradeRequestRepository upgradeRequestRepository;

    private UpgradeRequestRetrievalServiceImpl retrievalService;
    private UpgradeRequestStatusChangeServiceImpl statusChangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository);
        statusChangeService = new UpgradeRequestStatusChangeServiceImpl(upgradeRequestRepository);
    }

    @Test
    void testGetAllRequests() {
        List<UpgradeRequest> requests = List.of(new UpgradeRequest());
        when(upgradeRequestRepository.findAll()).thenReturn(requests);

        List<UpgradeRequest> result = retrievalService.getAllRequests();
        assertEquals(requests, result);
    }

    @Test
    void testGetRequestByUsername() {
        User user = new User();
        UpgradeRequest request = new UpgradeRequest();
        when(upgradeRequestRepository.findByRequesterUser(user)).thenReturn(Optional.of(request));

        Optional<UpgradeRequest> result = retrievalService.getRequestByUsername(user);
        assertTrue(result.isPresent());
        assertEquals(request, result.get());
    }

    @Test
    void testUpdateRequestStatus() {
        UUID id = UUID.randomUUID();
        UpgradeRequest request = new UpgradeRequest();
        when(upgradeRequestRepository.getReferenceById(id)).thenReturn(request);

        statusChangeService.updateRequestStatus(id.toString(), "APPROVED");

        assertEquals("APPROVED", request.getStatus());
        verify(upgradeRequestRepository).save(request);
    }
}
