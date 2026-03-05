package id.ac.ui.cs.advprog.jsonbackend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
    }

    @Test
    void testWebConfigIsNotNull() {
        assertNotNull(webConfig);
    }

    @Test
    void testWebConfigImplementsWebMvcConfigurer() {
        assertTrue(webConfig instanceof WebMvcConfigurer);
    }

    @Test
    void testAddCorsMappingsDoesNotThrow() {
        CorsRegistry registry = new CorsRegistry();
        assertDoesNotThrow(() -> webConfig.addCorsMappings(registry));
    }

    @Test
    void testCorsRegistrationApplied() {
        CorsRegistry registry = new CorsRegistry() {
            boolean mappingAdded = false;

            @Override
            public CorsRegistration addMapping(String pathPattern) {
                mappingAdded = true;
                assertEquals("/api/**", pathPattern);
                return super.addMapping(pathPattern);
            }
        };

        webConfig.addCorsMappings(registry);
    }

}

