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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:AuthProfileTestFinal;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER")
class AuthProfileApiIntegrationTest {

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

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        upgradeRepo.deleteAll();
        userRepository.deleteAll();

        adminUser = User.builder()
                .username("admin_auth_fin")
                .email("admin_auth_fin@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .fullName("Admin User")
                .build();
        userRepository.save(adminUser);

        regularUser = User.builder()
                .username("user_auth_fin")
                .email("user_auth_fin@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .fullName("Regular User")
                .build();
        userRepository.save(regularUser);
    }

    @Test
    void testRegistrationAndProfileWorkflow() throws Exception {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        UserRegistrationRequest regReq = new UserRegistrationRequest();
        regReq.setUsername("newuser_" + randomSuffix);
        regReq.setEmail("new_" + randomSuffix + "@test.com");
        regReq.setPassword("Password123!");
        regReq.setConfirmPassword("Password123!");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        UserLoginRequest loginReq = new UserLoginRequest();
        loginReq.setEmail(regReq.getEmail());
        loginReq.setPassword("Password123!");

        String response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        UserProfileUpdateRequest updateReq = UserProfileUpdateRequest.builder()
                .fullName("Updated Name")
                .bio("New Bio")
                .build();

        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated Name")));
    }

    @Test
    void testAdminActions() throws Exception {
        String adminToken = jwtService.generateToken(adminUser);
        UUID userId = regularUser.getId();

        mockMvc.perform(patch("/api/user/" + userId + "/ban")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/profile/user_auth_fin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("BANNED")));
    }
}
