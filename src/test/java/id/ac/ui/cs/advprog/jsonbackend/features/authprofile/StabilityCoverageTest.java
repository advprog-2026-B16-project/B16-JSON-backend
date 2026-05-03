package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StabilityCoverageTest {

    @Mock private UpgradeRequestRepository upgradeRepo;
    @Mock private UserRepository userRepo;
    @Mock private UserService userService;
    @Mock private JdbcTemplate jdbcTemplate;

    @InjectMocks private UpgradeRequestRetrievalServiceImpl retrievalService;
    @InjectMocks private UpgradeRequestStatusChangeServiceImpl statusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrievalRobustBranches() throws Exception {
        // 1. Fallback trigger
        when(upgradeRepo.findAll()).thenThrow(new RuntimeException("JPA ERROR"));
        
        // 2. Mock ResultSet for mapRow branches
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject(1)).thenReturn(UUID.randomUUID());
        when(rs.getObject("created_at")).thenReturn(Timestamp.from(Instant.now()));
        when(rs.getString("credential")).thenReturn("C");
        when(rs.getString("full_name")).thenReturn("F");
        when(rs.getString("social_media_url")).thenReturn("S");
        when(rs.getString("status")).thenReturn("P");
        when(rs.getObject("requester_user")).thenReturn(UUID.randomUUID().toString());
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(inv -> {
            RowMapper<UpgradeRequest> mapper = inv.getArgument(1);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        });

        assertNotNull(retrievalService.getAllRequests());

        // 3. Cover more mapRow branches (OffsetDateTime path)
        reset(rs);
        when(rs.getObject("created_at")).thenReturn(OffsetDateTime.now());
        retrievalService.getAllRequests();
        
        // 4. Cover else branch for created_at
        reset(rs);
        when(rs.getObject("created_at")).thenReturn(null);
        retrievalService.getAllRequests();
    }

    @Test
    void testGetByUsernameRobustBranches() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        when(upgradeRepo.findByRequesterUser(user)).thenThrow(new RuntimeException("JPA ERROR"));
        
        ResultSet rs = mock(ResultSet.class);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenAnswer(inv -> {
            RowMapper<UpgradeRequest> mapper = inv.getArgument(1);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        });
        
        assertTrue(retrievalService.getRequestByUsername(user).isPresent());
    }

    @Test
    void testStatusChangeRobustBranches() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("F"); dto.setCredential("C"); dto.setSocialMediaUrl("S");

        // 1. Pre-submit fallback
        when(upgradeRepo.findByRequesterUser(user)).thenThrow(new RuntimeException("JPA ERROR"));
        // 2. Save fallback
        when(upgradeRepo.save(any())).thenThrow(new RuntimeException("SAVE ERROR"));
        
        assertNotNull(statusService.submitUpgradeRequest(user, dto));
        
        // 3. UpdateStatus fallback
        UUID id = UUID.randomUUID();
        when(upgradeRepo.findById(anyString())).thenThrow(new RuntimeException("JPA ERROR"));
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenAnswer(inv -> {
            RowMapper<String> mapper = inv.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString(1)).thenReturn(UUID.randomUUID().toString());
            return Collections.singletonList(mapper.mapRow(rs, 0));
        });
        when(userRepo.findById(any(UUID.class))).thenReturn(Optional.of(user));
        
        
        statusService.updateRequestStatus(id, "ACCEPTED");
        
        // 4. UpdateStatus fallback - Empty results (Covering line 90)
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> statusService.updateRequestStatus(id, "ACCEPTED"));
        
    }

    @Test
    void testPrivateMethods() {
        // retrievalService.getUuidString
        // assertNull(ReflectionTestUtils.invokeMethod(retrievalService, "getUuidString", null));
        assertEquals("test", ReflectionTestUtils.invokeMethod(retrievalService, "getUuidString", "test"));

        // retrievalService.parseUuid
        // assertNull(ReflectionTestUtils.invokeMethod(retrievalService, "parseUuid", null));
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, ReflectionTestUtils.invokeMethod(retrievalService, "parseUuid", uuid));
        assertEquals(uuid, ReflectionTestUtils.invokeMethod(retrievalService, "parseUuid", uuid.toString()));
        assertNull(ReflectionTestUtils.invokeMethod(retrievalService, "parseUuid", "invalid"));
    }
}
