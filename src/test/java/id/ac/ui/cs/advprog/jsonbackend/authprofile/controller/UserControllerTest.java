package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.StubUserServiceConfig.class)
class UserControllerTest {

    @TestConfiguration
    static class StubUserServiceConfig {

        @Bean
        UserService userService() {
            return new StubUserService();
        }
    }

    static class StubUserService implements UserService {

        private List<User> users = new ArrayList<>();

        void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public List<User> getAllUsers() {
            return users;
        }

        @Override
        public Optional<User> getUserByUsername(String username) {
            return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
        }

        @Override
        public Optional<User> getUserByEmail(String email) {
            return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public User saveUser(User user) {
            return user;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("john", "john@example.com", "pass1", UserRole.TITIPER, UserStatus.ACTIVE);
        user2 = new User("jane", "jane@example.com", "pass2", UserRole.JASTIPER, UserStatus.ACTIVE);

        ((StubUserService) userService).setUsers(new ArrayList<>());
    }

    @Test
    void testUserControllerNotNull() {
        assertNotNull(userController);
    }

    @Test
    void testGetUsersReturnsOk() throws Exception {
        mockMvc.perform(get("/api/user/getUsers"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUsersReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/user/getUsers"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetUsersReturnsAllUsers() throws Exception {
        ((StubUserService) userService).setUsers(List.of(user1, user2));

        mockMvc.perform(get("/api/user/getUsers"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].username").value("jane"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }

    @Test
    void testGetUsersReturnsSingleUser() throws Exception {
        ((StubUserService) userService).setUsers(List.of(user1));

        mockMvc.perform(get("/api/user/getUsers"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("john"));
    }

}
