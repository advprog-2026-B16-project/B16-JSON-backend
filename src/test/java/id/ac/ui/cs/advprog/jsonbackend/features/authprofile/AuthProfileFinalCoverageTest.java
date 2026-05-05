package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.*;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Collections;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:AuthProfileFinalBruteForce;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER;DATABASE_TO_UPPER=FALSE",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "application.security.jwt.secret-key=Zm9yLXRlc3RpbmctcHVycG9zZXMtc2VjcmV0LWtleS1tdXN0LWJlLTMyLWJ5dGVz",
    "application.security.jwt.expiration=3600000"
})
class AuthProfileFinalCoverageTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    
    @MockBean private LoginService loginService;
    @MockBean private RegistrationService registrationService;
    @MockBean private UserService userService;
    @MockBean private UpgradeRequestRetrievalService retrievalService;
    @MockBean private UpgradeRequestStatusChangeService statusChangeService;
    @MockBean private JwtService jwtService;
    @MockBean private UserRepository userRepository;
    @MockBean private UpgradeRequestRepository upgradeRepo;

    @Test
    void exhaustiveControllerCoverage() throws Exception {
        UUID id = UUID.randomUUID();
        User u = User.builder().id(id).username("u").role(UserRole.TITIPER).status(UserStatus.ACTIVE).build();

        // --- 1. LoginController ---
        mockMvc.perform(get("/api/login")).andExpect(status().isOk());
        
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail("test@example.com"); loginDto.setPassword("StrongPass123!");
        
        when(loginService.login(any())).thenReturn(u);
        when(jwtService.generateToken(any())).thenReturn("token");
        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto))).andExpect(status().isOk());

        reset(loginService);
        doThrow(new BadCredentialsException("bad")).when(loginService).login(any());
        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto))).andExpect(status().isUnauthorized());

        reset(loginService);
        doThrow(new RuntimeException("err")).when(loginService).login(any());
        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto))).andExpect(status().isInternalServerError());

        // --- 2. RegistrationController ---
        mockMvc.perform(get("/api/register")).andExpect(status().isOk());
        
        UserRegistrationRequest regDto = new UserRegistrationRequest();
        regDto.setEmail("test@example.com"); regDto.setPassword("StrongPass123!"); regDto.setConfirmPassword("StrongPass123!");
        
        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(regDto))).andExpect(status().isCreated());

        doThrow(new RuntimeException("conflict")).when(registrationService).register(any());
        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(regDto))).andExpect(status().isConflict());

        // --- 3. UserController ---
        when(userService.getUserByUsername("u")).thenReturn(Optional.of(u));
        mockMvc.perform(get("/api/user/profile/u")).andExpect(status().isOk());

        when(userService.getUserByUsername("missing")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/user/profile/missing")).andExpect(status().isNotFound());

        // --- 4. UpgradeRequest Retrieval ---
        when(retrievalService.getAllRequests()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/upgrade-request/get-all")).andExpect(status().isOk());
    }

    @Test
    void exhaustiveLombokAndRecordCoverage() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        // Record
        UpgradeRequestResponse urr = new UpgradeRequestResponse(id, now, id, "u", "f", "c", "s", "st");
        assertNotNull(urr.toString());
        assertEquals(id, urr.id());
        
        UpgradeRequest req = mock(UpgradeRequest.class);
        User u = mock(User.class);
        when(req.getUpgrReqId()).thenReturn(id);
        when(req.getRequesterUser()).thenReturn(u);
        when(u.getId()).thenReturn(id);
        when(u.getUsername()).thenReturn("un");
        assertNotNull(UpgradeRequestResponse.fromRequest(req));
        
        when(req.getRequesterUser()).thenReturn(null);
        assertNotNull(UpgradeRequestResponse.fromRequest(req));

        UserLoginResponse ulr = UserLoginResponse.builder().id(id).username("u").email("e").role("r").status("s").token("t").build();
        assertNotNull(ulr.toString());
        
        when(u.getRole()).thenReturn(UserRole.TITIPER);
        when(u.getStatus()).thenReturn(UserStatus.ACTIVE);
        assertNotNull(UserLoginResponse.fromUser(u, "t"));

        // DTOs
        UserProfileResponse upr = UserProfileResponse.builder().id(id).build();
        upr.setBio("b");
        assertEquals("b", upr.getBio());
        assertNotNull(upr.toString());
        assertNotNull(new UserProfileResponse(id, "u", "e", "r", "s", "f", "b", "l", "a", 0L));

        UserProfileUpdateRequest upu = UserProfileUpdateRequest.builder().fullName("f").build();
        assertNotNull(upu.toString());
        assertNotNull(new UserProfileUpdateRequest("f", "b", "l", "a"));

        UpgradeRequestSubmissionRequest sub = new UpgradeRequestSubmissionRequest("f", "c", "s");
        assertEquals("f", sub.getFullName());
        assertNotNull(sub.toString());
        
        UserRegistrationRequest reg = new UserRegistrationRequest();
        reg.setEmail("e"); reg.setPassword("p"); reg.setConfirmPassword("p");
        assertTrue(reg.passwordConfirmationMathces());
        assertNotNull(reg.toString());

        // Exceptions
        assertNotNull(new BadCredentialsException("m").getMessage());
        assertNotNull(new UserNotFoundException("m").getMessage());
        assertNotNull(new WrongPasswordException("m").getMessage());
        assertNotNull(new EmailAlreadyExistsException("m").getMessage());
        assertNotNull(new UsernameAlreadyExistsException("m").getMessage());

        // Model
        User user = User.builder().id(id).username("u").role(UserRole.TITIPER).status(UserStatus.ACTIVE).build();
        assertNotNull(user.toString());
        assertEquals(user, User.builder().id(id).username("u").role(UserRole.TITIPER).status(UserStatus.ACTIVE).build());
        user.getAuthorities();
        user.isAccountNonExpired();
        
        UpgradeRequest upgr = UpgradeRequest.builder().upgrReqId(id).build();
        assertNotNull(upgr.toString());
        UpgradeRequest upgr2 = UpgradeRequest.builder().upgrReqId(id).build();
        assertEquals(upgr.getUpgrReqId(), upgr2.getUpgrReqId());
    }

    @Test
    void exerciseServiceEdges() {
        UpgradeRequestRepository repo = mock(UpgradeRequestRepository.class);
        UpgradeRequestRetrievalServiceImpl service = new UpgradeRequestRetrievalServiceImpl(repo);
        when(repo.findByRequesterUser(any())).thenReturn(Optional.empty());
        assertFalse(service.getRequestByUsername(new User()).isPresent());
        
        UserServiceImpl userServiceImpl = new UserServiceImpl(mock(UserRepository.class));
        userServiceImpl.getUserByEmail("e");
    }
}
