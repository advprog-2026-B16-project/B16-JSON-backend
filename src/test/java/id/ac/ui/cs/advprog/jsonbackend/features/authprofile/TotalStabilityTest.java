package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

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

class TotalStabilityTest {

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
    void testRetrievalService_Exhaustive() throws Exception {
        // Mock result set for RowMapper
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("upgr_req_id")).thenReturn("id1");
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

        // getAllRequests
        
        assertNotNull(retrievalService.getAllRequests());
        
        // Cover parseUuid invalid path
        reset(rs);
        when(rs.getObject("requester_user")).thenReturn("not-a-uuid");
        retrievalService.getAllRequests();
        

        // getRequestByUsername
        User user = new User();
        user.setId(UUID.randomUUID());
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenAnswer(inv -> {
            RowMapper<UpgradeRequest> mapper = inv.getArgument(1);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        });
        assertTrue(retrievalService.getRequestByUsername(user).isPresent());
        
        // Cover branches in mapRow
        reset(rs);
        when(rs.getObject("created_at")).thenReturn(OffsetDateTime.now());
        retrievalService.getAllRequests();
        
        reset(rs);
        when(rs.getObject("created_at")).thenReturn(null);
        retrievalService.getAllRequests();
    }

    @Test
    void testStatusService_Exhaustive() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test");
        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("F"); dto.setCredential("C"); dto.setSocialMediaUrl("S");

        // 1. Submit - Success
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenReturn(Collections.singletonList("ACCEPTED"));
        assertNotNull(statusService.submitUpgradeRequest(user, dto));
        verify(jdbcTemplate).update(contains("INSERT"), any(), any(), any(), any(), any(), any());

        // 2. Submit - Pending error
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any())).thenReturn(Collections.singletonList("PENDING"));
        assertThrows(RuntimeException.class, () -> statusService.submitUpgradeRequest(user, dto));

        // 3. updateRequestStatus - Success
        UUID id = UUID.randomUUID();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(Collections.singletonList(UUID.randomUUID().toString()));
        when(userRepo.findById(any(UUID.class))).thenReturn(Optional.of(user));
        
        statusService.updateRequestStatus(id, "ACCEPTED");
        verify(jdbcTemplate).update(contains("UPDATE"), eq("ACCEPTED"), eq(id.toString()));

        // 4. updateRequestStatus - Not found
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> statusService.updateRequestStatus(id, "ACCEPTED"));
    }
}
