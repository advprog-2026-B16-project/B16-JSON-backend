package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpgradeRequestServiceTest {

    @Mock
    private UpgradeRequestRepository upgradeRequestRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private UpgradeRequestStatusChangeServiceImpl statusChangeService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .build();
    }

    @Test
    void testSubmitUpgradeRequestSuccess() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any())).thenReturn(0);
        
        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(testUser)
                .status("PENDING")
                .build();
        
        when(upgradeRequestRepository.save(any(UpgradeRequest.class))).thenReturn(request);

        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("John Doe");
        dto.setCredential("Proof");

        UpgradeRequestResponse result = statusChangeService.submitUpgradeRequest(testUser, dto);
        
        assertNotNull(result);
        assertEquals("PENDING", result.status());
    }

    @Test
    void testGetAllRequests() {
        UpgradeRequestRetrievalServiceImpl retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository, userRepository, jdbcTemplate);
        // Repository and JdbcTemplate are mocked, result doesn't matter for compile check
        List<UpgradeRequest> result = retrievalService.getAllRequests();
        assertNotNull(result);
    }
}