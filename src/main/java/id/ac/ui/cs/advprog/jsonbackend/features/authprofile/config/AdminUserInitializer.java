package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.user.email:}")
    private String adminEmail;

    @Value("${admin.user.username:admin}")
    private String adminUsername;

    @Value("${admin.user.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (isBlank(adminEmail) || isBlank(adminPassword)) {
            return;
        }

        userRepository.findByEmail(adminEmail)
                .map(this::promoteExistingUser)
                .orElseGet(this::createAdminUser);
    }

    private User promoteExistingUser(User user) {
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private User createAdminUser() {
        String username = isBlank(adminUsername) ? adminEmail.split("@")[0] : adminUsername;
        User user = User.builder()
                .username(username)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
