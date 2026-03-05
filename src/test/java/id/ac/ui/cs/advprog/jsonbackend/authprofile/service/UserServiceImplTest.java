package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private StubUserRepository stubUserRepository;
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        stubUserRepository = new StubUserRepository();
        userService = new UserServiceImpl(stubUserRepository);

        user1 = new User("john", "john@example.com", "pass1", UserRole.TITIPER, UserStatus.ACTIVE);
        user2 = new User("jane", "jane@example.com", "pass2", UserRole.JASTIPER, UserStatus.ACTIVE);
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
        assertEquals("john", result.get(0).getUsername());
        assertEquals("john@example.com", result.get(0).getEmail());
    }

    @Test
    void testGetAllUsersReturnsAllUsers() {
        stubUserRepository.setUsers(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("john", result.get(0).getUsername());
        assertEquals("jane", result.get(1).getUsername());
    }

    @Test
    void testGetUserByUsername() {
        stubUserRepository.setUsers(List.of(user1));

        Optional<User> result = userService.getUserByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void testGetUserByEmail() {
        stubUserRepository.setUsers(List.of(user1));

        Optional<User> result = userService.getUserByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void testSaveUser() {
        User savedUser = userService.saveUser(user1);
        assertNotNull(savedUser);
        assertEquals("john", savedUser.getUsername());
    }
}
