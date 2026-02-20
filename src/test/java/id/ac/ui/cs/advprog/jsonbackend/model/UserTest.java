package id.ac.ui.cs.advprog.jsonbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        var constructor = User.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        user = constructor.newInstance();
    }

    @Test
    void testDefaultUserIdIsNull() {
        assertNull(user.getUserId());
    }

    @Test
    void testDefaultFirstNameIsNull() {
        assertNull(user.getFirstName());
    }

    @Test
    void testDefaultLastNameIsNull() {
        assertNull(user.getLastName());
    }

    @Test
    void testSetAndGetUserId() {
        user.setUserId(1L);
        assertEquals(1L, user.getUserId());
    }

    @Test
    void testSetAndGetFirstName() {
        user.setFirstName("John");
        assertEquals("John", user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void testSetUserIdOverwrite() {
        user.setUserId(1L);
        user.setUserId(2L);
        assertEquals(2L, user.getUserId());
    }

    @Test
    void testSetFirstNameOverwrite() {
        user.setFirstName("John");
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void testSetLastNameOverwrite() {
        user.setLastName("Doe");
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void testConstructorWithParameters() throws Exception {
        var constructor = User.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        User userWithParams = constructor.newInstance("Jane", "Smith");

        assertEquals("Jane", userWithParams.getFirstName());
        assertEquals("Smith", userWithParams.getLastName());
    }

    @Test
    void testToStringWithAllValues() {
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("User [userId=1, firstName=John, lastName=Doe]", user.toString());
    }

    @Test
    void testToStringWithNullValues() {
        assertEquals("User [userId=null, firstName=null, lastName=null]", user.toString());
    }

    @Test
    void testToStringWithPartialValues() {
        user.setUserId(5L);
        user.setFirstName("Alice");
        assertEquals("User [userId=5, firstName=Alice, lastName=null]", user.toString());
    }

}
