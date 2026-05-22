package id.ac.ui.cs.advprog.jsonbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JsonBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonBackendApplication.class, args);
    }

}
