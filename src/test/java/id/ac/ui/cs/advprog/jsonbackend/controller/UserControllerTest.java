package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
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
    void setUp() throws Exception {
        var constructor = User.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        user1 = constructor.newInstance();
        user1.setUserId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");

        user2 = constructor.newInstance();
        user2.setUserId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

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
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    void testGetUsersReturnsSingleUser() throws Exception {
        ((StubUserService) userService).setUsers(List.of(user1));

        mockMvc.perform(get("/api/user/getUsers"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

}
