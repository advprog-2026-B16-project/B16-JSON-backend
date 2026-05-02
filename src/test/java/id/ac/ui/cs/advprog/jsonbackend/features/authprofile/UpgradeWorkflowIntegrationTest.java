package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.*;
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
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:UpgradeWorkflowTestFinal;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER",
    "app.debug.verbose=true"
})
class UpgradeWorkflowIntegrationTest {

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

    private User admin;
    private User titiper;

    @BeforeEach
    void setUp() {
        upgradeRepo.deleteAll();
        userRepository.deleteAll();

        admin = User.builder()
                .username("admin_up_final")
                .email("admin_up_final@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        titiper = User.builder()
                .username("titiper_up_final")
                .email("titiper_up_final@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(titiper);
    }

    @Test
    void testUpgradeWorkflowAndRetrieval() throws Exception {
        String adminToken = jwtService.generateToken(admin);
        String userToken = jwtService.generateToken(titiper);

        UpgradeRequestSubmissionRequest submitDto = new UpgradeRequestSubmissionRequest();
        submitDto.setFullName("John Titiper");
        submitDto.setCredential("Proof");

        // 1. Submit
        mockMvc.perform(post("/api/upgrade-request/submit")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        // 2. Submit duplicate
        mockMvc.perform(post("/api/upgrade-request/submit")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDto)))
                .andExpect(status().isInternalServerError());

        // 3. Get All
        String requestsJson = mockMvc.perform(get("/api/upgrade-request/get-all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        UpgradeRequestResponse[] responses = objectMapper.readValue(requestsJson, UpgradeRequestResponse[].class);
        UUID requestId = responses[0].id();

        // 4. Approve
        UpgradeRequestStatusChangeRequest statusDto = new UpgradeRequestStatusChangeRequest();
        statusDto.setNewStatus("ACCEPTED");
        statusDto.setUsername("titiper_up_final");

        mockMvc.perform(patch("/api/upgrade-request/change-status/" + requestId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findByUsername("titiper_up_final").get();
        assertEquals(UserRole.JASTIPER, updatedUser.getRole());
        
        // 5. Submit again after Approval (Covering the "Not Pending" branch in submit)
        mockMvc.perform(post("/api/upgrade-request/submit")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatusNotFound() throws Exception {
        String adminToken = jwtService.generateToken(admin);
        UUID fakeId = UUID.randomUUID();
        
        UpgradeRequestStatusChangeRequest statusDto = new UpgradeRequestStatusChangeRequest();
        statusDto.setNewStatus("ACCEPTED");
        statusDto.setUsername("anyone");

        mockMvc.perform(patch("/api/upgrade-request/change-status/" + fakeId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isInternalServerError());
    }
}
