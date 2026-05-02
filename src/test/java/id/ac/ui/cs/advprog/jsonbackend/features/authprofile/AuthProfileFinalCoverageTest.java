package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller.UpgradeRequestRetrievalController;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository; import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalServiceImpl;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeServiceImpl;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthProfileFinalCoverageTest {

    @Mock private UpgradeRequestRepository upgradeRepo; @Mock private UserRepository userRepository;
    @Mock private UpgradeRequestRetrievalService retrievalService;
    @Mock private UserService userService;
    
    @InjectMocks private UpgradeRequestRetrievalServiceImpl retrievalServiceImpl;
    @InjectMocks private UpgradeRequestStatusChangeServiceImpl statusChangeServiceImpl;
    @InjectMocks private UpgradeRequestRetrievalController retrievalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpgradeRequestRetrievalService_GetRequestByUsername() {
        User user = new User();
        when(upgradeRepo.findByRequesterUser(user)).thenReturn(Optional.empty());
        assertFalse(retrievalServiceImpl.getRequestByUsername(user).isPresent());
        verify(upgradeRepo).findByRequesterUser(user);
    }

    @Test
    void testUpgradeRequestRetrievalController_VerboseLoggingBranches() {
        // 1. Test with verboseLogging = true
        ReflectionTestUtils.setField(retrievalController, "verboseLogging", true);
        when(retrievalService.getAllRequests()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<UpgradeRequestResponse>> res1 = retrievalController.getAllRequests();
        assertEquals(200, res1.getStatusCode().value());

        // 2. Test with verboseLogging = false
        ReflectionTestUtils.setField(retrievalController, "verboseLogging", false);
        ResponseEntity<List<UpgradeRequestResponse>> res2 = retrievalController.getAllRequests();
        assertEquals(200, res2.getStatusCode().value());
    }

    @Test
    void testUpgradeRequestStatusChangeService_UpdateStatusBranches() {
        UUID id = UUID.randomUUID();
        UpgradeRequest r = new UpgradeRequest();
        r.setUpgrReqId(id);
        User user = new User();
        r.setRequesterUser(user);
        
        when(upgradeRepo.findById(id)).thenReturn(Optional.of(r));

        // 1. Status = ACCEPTED (True branch)
        statusChangeServiceImpl.updateRequestStatus(id, "ACCEPTED");
        verify(userService).promoteToJastiper(user);
        verify(upgradeRepo).save(r);

        // 2. Status = REJECTED (False branch)
        reset(userService, upgradeRepo);
        when(upgradeRepo.findById(id)).thenReturn(Optional.of(r));
        statusChangeServiceImpl.updateRequestStatus(id, "REJECTED");
        verify(userService, never()).promoteToJastiper(any());
        verify(upgradeRepo).save(r);
    }

    @Test
    void testUpgradeRequestStatusChangeService_SubmitRequestBranches() {
        User user = new User();
        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("John");
        dto.setCredential("Proof"); dto.setSocialMediaUrl("url");

        // 1. Existing request NOT PENDING (False branch of L25)
        UpgradeRequest existing = new UpgradeRequest();
        existing.setStatus("ACCEPTED");
        when(upgradeRepo.findByRequesterUser(user)).thenReturn(Optional.of(existing));
        when(upgradeRepo.save(any())).thenReturn(existing);
        
        assertNotNull(statusChangeServiceImpl.submitUpgradeRequest(user, dto));

        // 2. Existing request IS PENDING (True branch of L25 -> Exception)
        existing.setStatus("PENDING");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> statusChangeServiceImpl.submitUpgradeRequest(user, dto));
        assertEquals("Pending request exists", ex.getMessage());
    }
}
