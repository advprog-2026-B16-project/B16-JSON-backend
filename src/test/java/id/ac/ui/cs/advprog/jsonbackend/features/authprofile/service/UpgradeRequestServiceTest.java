package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository; import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository; import org.springframework.jdbc.core.JdbcTemplate;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; import org.springframework.test.util.ReflectionTestUtils;
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
    private UserService userService; @Mock private JdbcTemplate jdbcTemplate;

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
        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(testUser)
                .status("PENDING")
                .build();
        
        when(upgradeRequestRepository.findByRequesterUser(testUser)).thenReturn(Optional.empty());
        when(upgradeRequestRepository.save(any(UpgradeRequest.class))).thenReturn(request);

        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("John Doe");
        dto.setCredential("Proof");

        UpgradeRequestResponse result = statusChangeService.submitUpgradeRequest(testUser, dto);
        
        assertNotNull(result);
        assertEquals("PENDING", result.status());
    }

    @Test
    void testGetAllRequestsAndGetByUsername() {
        UpgradeRequestRetrievalServiceImpl retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository, userRepository, jdbcTemplate);
        
        // 1. getAllRequests
        
        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class))).thenAnswer(invocation -> {
            org.springframework.jdbc.core.RowMapper<UpgradeRequest> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getObject("upgr_req_id")).thenReturn(UUID.randomUUID());
            when(rs.getObject("created_at")).thenReturn(java.sql.Timestamp.from(java.time.Instant.now()));
            when(rs.getObject("requester_user")).thenReturn(UUID.randomUUID().toString());
            when(rs.getString("status")).thenReturn("PENDING");
            List<UpgradeRequest> list = new java.util.ArrayList<>();
            list.add(mapper.mapRow(rs, 0));
            return list;
        });
        assertNotNull(retrievalService.getAllRequests());

        // 2. getRequestByUsername
        when(upgradeRequestRepository.findByRequesterUser(testUser)).thenReturn(Optional.empty());
        
        assertFalse(retrievalService.getRequestByUsername(testUser).isPresent());

        // 3. getUuid coverage
        ReflectionTestUtils.invokeMethod(retrievalService, "getUuid", "invalid-uuid");
        ReflectionTestUtils.invokeMethod(retrievalService, "getUuid", UUID.randomUUID().toString());
        ReflectionTestUtils.invokeMethod(retrievalService, "getUuid", new Object());
        
    }
}
