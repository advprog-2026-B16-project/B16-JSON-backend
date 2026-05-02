package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller.UpgradeRequestRetrievalController;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:AuthProfileFinalFinal;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER")
class AuthProfileBruteForceCoverageTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UpgradeRequestRetrievalController retrievalController;
    @Autowired private UpgradeRequestStatusChangeService statusChangeService;
    @Autowired private UserRepository userRepository;
    @Autowired private UpgradeRequestRepository upgradeRepo;
    @Autowired private JwtService jwtService;

    private User admin;
    private User titiper;

    @BeforeEach
    void setUp() {
        upgradeRepo.deleteAll();
        userRepository.deleteAll();

        admin = User.builder()
                .username("admin_bf")
                .email("admin_bf@test.com")
                .password("pass")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        titiper = User.builder()
                .username("titiper_bf")
                .email("titiper_bf@test.com")
                .password("pass")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(titiper);
    }

    @Test
    void testUpgradeRequestRetrievalController_Branches() throws Exception {
        String adminToken = jwtService.generateToken(admin);

        // 1. verboseLogging = true
        ReflectionTestUtils.setField(retrievalController, "verboseLogging", true);
        mockMvc.perform(get("/api/upgrade-request/get-all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 2. verboseLogging = false
        ReflectionTestUtils.setField(retrievalController, "verboseLogging", false);
        mockMvc.perform(get("/api/upgrade-request/get-requests")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testUpgradeRequestStatusChangeService_Branches() {
        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest();
        dto.setFullName("Name");
        dto.setCredential("Cred");
        
        UpgradeRequestResponse res = statusChangeService.submitUpgradeRequest(titiper, dto);
        UUID requestId = res.id();

        // 1. ACCEPTED (promotes user)
        statusChangeService.updateRequestStatus(requestId, "ACCEPTED");
        assertEquals(UserRole.JASTIPER, userRepository.findByUsername("titiper_bf").get().getRole());

        // 2. REJECTED (does not promote user)
        User u2 = User.builder()
                .username("user2")
                .email("user2@test.com")
                .password("pass")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(u2);
        UpgradeRequestResponse res2 = statusChangeService.submitUpgradeRequest(u2, dto);
        
        statusChangeService.updateRequestStatus(res2.id(), "REJECTED");
        assertEquals(UserRole.TITIPER, userRepository.findByUsername("user2").get().getRole());
    }
}
