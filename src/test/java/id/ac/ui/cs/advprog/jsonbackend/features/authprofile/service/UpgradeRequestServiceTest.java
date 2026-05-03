package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository; import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void testSubmitUpgradeRequest_Success() {
        UpgradeRequestSubmissionRequest requestDto = new UpgradeRequestSubmissionRequest();
        requestDto.setFullName("John Doe");
        requestDto.setCredential("Some Credential");
        requestDto.setSocialMediaUrl("url");

        when(upgradeRequestRepository.findByRequesterUser(testUser)).thenReturn(Optional.empty());
        when(upgradeRequestRepository.save(any(UpgradeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpgradeRequestResponse result = statusChangeService.submitUpgradeRequest(testUser, requestDto);

        assertNotNull(result);
        assertEquals("PENDING", result.status());
    }

    @Test
    void testGetAllRequestsAndGetByUsername() {
        UpgradeRequestRetrievalServiceImpl retrievalService = new UpgradeRequestRetrievalServiceImpl(upgradeRequestRepository, userRepository, jdbcTemplate);

        when(upgradeRequestRepository.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(retrievalService.getAllRequests());

        when(upgradeRequestRepository.findByRequesterUser(testUser)).thenReturn(Optional.empty());
        assertFalse(retrievalService.getRequestByUsername(testUser).isPresent());
    }
}
