package id.ac.ui.cs.advprog.jsonbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DummyController.class)
class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyController dummyController;

    @Test
    void testSayHelloReturnsOk() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void testSayHelloReturnsCorrectBody() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(content().string("Hello from Backend!"));
    }

    @Test
    void testSayHelloReturnsCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void testSayHelloDirectCall() {
        assertEquals("Hello from Backend!", dummyController.sayHello());
    }

    @Test
    void testDummyControllerNotNull() {
        assertNotNull(dummyController);
    }

}
