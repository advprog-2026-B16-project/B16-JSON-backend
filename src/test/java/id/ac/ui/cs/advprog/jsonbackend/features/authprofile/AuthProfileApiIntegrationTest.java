package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
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
    private User jastiperUser;

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

        jastiperUser = User.builder()
                .username("jastiper")
                .email("jastiper@test.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.JASTIPER)
                .status(UserStatus.ACTIVE)
                .fullName("Jastiper User")
                .build();
        userRepository.save(jastiperUser);
    }

    @Test
    void testFullAuthWorkflow() throws Exception {
        // 1. Registration
        UserRegistrationRequest regReq = new UserRegistrationRequest();
        regReq.setUsername("newuser");
        regReq.setEmail("new@test.com");
        regReq.setPassword("password123");
        regReq.setConfirmPassword("password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("registered successfully")));

        // 2. Login
        UserLoginRequest loginReq = new UserLoginRequest();
        loginReq.setEmail("new@test.com");
        loginReq.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // 3. Get Own Profile
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("new@test.com")));

        // 4. Update Profile
        UserProfileUpdateRequest updateReq = UserProfileUpdateRequest.builder()
                .fullName("New Full Name")
                .bio("I love shopping")
                .location("Jakarta")
                .avatarUrl("http://img.com/me.jpg")
                .build();

        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());

        // Verify Update
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("New Full Name")))
                .andExpect(jsonPath("$.bio", is("I love shopping")));
    }

    @Test
    void testAdminMonitoringActions() throws Exception {
        String adminToken = jwtService.generateToken(adminUser);
        UUID userIdToBan = regularUser.getId();

        // 1. Get Users (Admin only)
        mockMvc.perform(get("/api/user/getUsers")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));

        // 2. Ban User
        mockMvc.perform(patch("/api/user/" + userIdToBan + "/ban")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify Status
        mockMvc.perform(get("/api/user/profile/user")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("BANNED")));

        // 3. Demote Jastiper
        mockMvc.perform(patch("/api/user/" + jastiperUser.getId() + "/demote")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify Role
        mockMvc.perform(get("/api/user/profile/jastiper")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("TITIPER")));
    }

    @Test
    void testSecurityConstraints() throws Exception {
        // Unauthorized access
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());

        // Regular user accessing admin endpoint
        String userToken = jwtService.generateToken(regularUser);
        mockMvc.perform(get("/api/user/getUsers")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPublicProfileView() throws Exception {
        String userToken = jwtService.generateToken(regularUser);
        
        mockMvc.perform(get("/api/user/profile/admin")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.fullName", is("Admin User")));
    }
}
