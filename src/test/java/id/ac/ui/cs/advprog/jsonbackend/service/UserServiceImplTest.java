package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private StubUserRepository stubUserRepository;
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() throws Exception {
        stubUserRepository = new StubUserRepository();
        userService = new UserServiceImpl(stubUserRepository);

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
    }

    @Test
    void testUserServiceNotNull() {
        assertNotNull(userService);
    }

    @Test
    void testGetAllUsersReturnsEmptyList() {
        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUsersReturnsSingleUser() {
        stubUserRepository.setUsers(List.of(user1));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
    }

    @Test
    void testGetAllUsersReturnsAllUsers() {
        stubUserRepository.setUsers(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

}
