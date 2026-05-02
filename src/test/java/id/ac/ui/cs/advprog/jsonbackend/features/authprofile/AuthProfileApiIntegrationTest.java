package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@org.springframework.test.context.ActiveProfiles("test")
class AuthProfileApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();

        adminUser = User.builder()
                .username("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .fullName("Admin User")
                .build();
        userRepository.save(adminUser);

        regularUser = User.builder()
                .username("user")
                .email("user@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .fullName("Regular User")
                .build();
        userRepository.save(regularUser);
    }

    @Test
    void testRegistrationAndProfileWorkflow() throws Exception {
        UserRegistrationRequest regReq = new UserRegistrationRequest();
        regReq.setUsername("newuser");
        regReq.setEmail("new@test.com");
        regReq.setPassword("Password123!");
        regReq.setConfirmPassword("Password123!");
        

        // 1. Register
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // 2. Login
        UserLoginRequest loginReq = new UserLoginRequest();
        loginReq.setEmail("new@test.com");
        loginReq.setPassword("Password123!");

        String response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // 3. Update Profile
        UserProfileUpdateRequest updateReq = UserProfileUpdateRequest.builder()
                .fullName("Updated Name")
                .bio("New Bio")
                .build();

        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());
                
        // 4. Verify
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

        mockMvc.perform(get("/api/user/profile/user")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("BANNED")));
    }
}
