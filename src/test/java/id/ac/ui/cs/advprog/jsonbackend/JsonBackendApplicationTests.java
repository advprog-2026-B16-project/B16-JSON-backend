package id.ac.ui.cs.advprog.jsonbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JsonBackendApplicationTests {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void applicationClassExists() {
        assertNotNull(JsonBackendApplication.class);
    }

    @Test
    void applicationContextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        var method = JsonBackendApplication.class.getMethod("main", String[].class);
        assertNotNull(method);
    }

}
