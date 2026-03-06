package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

@Service
public class SanitizationService {

    private final PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return policy.sanitize(input);
    }
}
