package id.ac.ui.cs.advprog.jsonbackend.features.authprofile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
class SchemaDebug {
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void debugSchema() {
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'upgrade_request'");
            System.out.println("COLUMNS FOR upgrade_request: " + columns);
            
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
            System.out.println("ALL TABLES: " + tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
