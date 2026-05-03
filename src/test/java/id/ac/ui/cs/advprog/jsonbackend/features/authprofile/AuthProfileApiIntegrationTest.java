package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:AuthProfileUltimateTest;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER;DATABASE_TO_UPPER=FALSE",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "app.debug.verbose=true"
})
class AuthProfileApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private UpgradeRequestRepository upgradeRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        upgradeRepo.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testAuthProfileCompleteLifecycle() throws Exception {
        // --- 1. REGISTRATION ---
        UserRegistrationRequest regDto = new UserRegistrationRequest();
        regDto.setEmail("tester@example.com");
        regDto.setPassword("StrongPass123!");
        regDto.setConfirmPassword("StrongPass123!");
        
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regDto)))
                .andExpect(status().isCreated());

        // Verify Auto-username
        User user = userRepository.findByEmail("tester@example.com").orElseThrow();
        assertEquals("tester", user.getUsername());

        // --- 2. LOGIN (SUCCESS & FAILURE) ---
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail("tester@example.com");
        loginDto.setPassword("StrongPass123!");
        
        String response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();
        
        String token = objectMapper.readValue(response, UserLoginResponse.class).token();

        // Login Failure
        loginDto.setPassword("WrongPass");
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());

        // --- 3. PROFILE MANAGEMENT ---
        UserProfileUpdateRequest updateDto = new UserProfileUpdateRequest();
        updateDto.setFullName("Updated Name");
        updateDto.setBio("Bio Here");
        
        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        // Get own profile
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated Name")));

        // --- 4. KYC WORKFLOW ---
        UpgradeRequestSubmissionRequest kycDto = new UpgradeRequestSubmissionRequest();
        kycDto.setFullName("Real Name");
        kycDto.setCredential("Passport");
        kycDto.setSocialMediaUrl("http://ig.com/tester");
        
        mockMvc.perform(post("/api/upgrade-request/submit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kycDto)))
                .andExpect(status().isOk());
        
        // Check Status Transition
        user = userRepository.findByUsername("tester").get();
        assertEquals(UserStatus.PENDING_JASTIPER, user.getStatus());

        // Duplicate Submit (Should fail 500 or 400 depending on implementation)
        mockMvc.perform(post("/api/upgrade-request/submit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kycDto)))
                .andExpect(status().isInternalServerError());

        // --- 5. ADMIN PORTAL ---
        User admin = User.builder()
                .username("admin")
                .email("admin@json.com")
                .password(passwordEncoder.encode("AdminPass123!"))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(admin);

        String adminToken = objectMapper.readValue(
            mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserLoginRequest() {{
                    setEmail("admin@json.com"); setPassword("AdminPass123!");
                }})))
                .andReturn().getResponse().getContentAsString(),
            UserLoginResponse.class).token();

        // Admin List requests
        String reqListJson = mockMvc.perform(get("/api/upgrade-request/get-all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn().getResponse().getContentAsString();
        
        UpgradeRequestResponse kycRes = objectMapper.readValue(reqListJson, UpgradeRequestResponse[].class)[0];
        
        // Admin Approve
        UpgradeRequestStatusChangeRequest decisionDto = new UpgradeRequestStatusChangeRequest();
        decisionDto.setNewStatus("ACCEPTED");
        decisionDto.setUsername("tester");
        
        mockMvc.perform(patch("/api/upgrade-request/change-status/" + kycRes.id())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decisionDto)))
                .andExpect(status().isOk());
        
        // Verify Role Promotion
        user = userRepository.findByUsername("tester").get();
        assertEquals(UserRole.JASTIPER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        // --- 6. PUBLIC PROFILE ---
        mockMvc.perform(get("/api/user/profile/tester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("JASTIPER")))
                .andExpect(jsonPath("$.successfulTransactions").exists());

        // --- 7. ADMIN USER MANAGEMENT ---
        // Ban
        mockMvc.perform(patch("/api/user/" + user.getId() + "/ban")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        
        assertEquals(UserStatus.BANNED, userRepository.findByUsername("tester").get().getStatus());

        // Demote
        mockMvc.perform(patch("/api/user/" + user.getId() + "/demote")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        
        assertEquals(UserRole.TITIPER, userRepository.findByUsername("tester").get().getRole());
    }
}
