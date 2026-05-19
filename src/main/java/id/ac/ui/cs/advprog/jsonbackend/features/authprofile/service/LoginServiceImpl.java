package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserLoggedInEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.common.config.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public User login(UserLoginRequest dto) {
        String dtoEmail = dto.getEmail();
        
        if (loginAttemptService.isBlocked(dtoEmail)) {
            throw new BadCredentialsException("Account is locked due to too many failed attempts. Try again in 5 minutes.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(dtoEmail);

        if (optionalUser.isEmpty()) {
            loginAttemptService.loginFailed(dtoEmail);
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(dtoEmail);
            throw new BadCredentialsException("Invalid credentials");
        }

        loginAttemptService.loginSucceeded(dtoEmail);
        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId().toString()));
        return user;
    }
}
