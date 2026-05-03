package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:FinalBranchTest;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER")
class FinalBranchCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpgradeRequestRepository upgradeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UpgradeRequestStatusChangeService statusChangeService;

    private User admin;
    private User titiper;

    @BeforeEach
    void setUp() {
        upgradeRepo.deleteAll();
        userRepository.deleteAll();

        admin = User.builder()
                .username("admin_branch")
                .email("admin_branch@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        titiper = User.builder()
                .username("titiper_branch")
                .email("titiper_branch@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(titiper);
    }

    @Test
    void testRetrievalWithVerboseFalse() throws Exception {
        String adminToken = jwtService.generateToken(admin);
        mockMvc.perform(get("/api/upgrade-request/get-all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testRejectRequest() throws Exception {
        UpgradeRequestSubmissionRequest submitDto = new UpgradeRequestSubmissionRequest();
        submitDto.setFullName("John");
        submitDto.setCredential("Proof");
        
        UpgradeRequestResponse res = statusChangeService.submitUpgradeRequest(titiper, submitDto);
        String requestId = res.id();

        UpgradeRequestStatusChangeRequest statusDto = new UpgradeRequestStatusChangeRequest();
        statusDto.setNewStatus("REJECTED");
        statusDto.setUsername("titiper_branch");

        String adminToken = jwtService.generateToken(admin);
        mockMvc.perform(patch("/api/upgrade-request/change-status/" + requestId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("titiper_branch").get();
        assertEquals(UserRole.TITIPER, user.getRole());
    }
}
