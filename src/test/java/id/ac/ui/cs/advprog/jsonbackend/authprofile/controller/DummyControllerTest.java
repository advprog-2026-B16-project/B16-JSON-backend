package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DummyControllerTest {

    private DummyController dummyController;

    @BeforeEach
    void setUp() {
        dummyController = new DummyController();
    }

    @Test
    void testSayHelloReturnsCorrectBody() {
        assertEquals("Hello from Backend!", dummyController.sayHello());
    }

    @Test
    void testDummyControllerNotNull() {
        assertNotNull(dummyController);
    }
}
