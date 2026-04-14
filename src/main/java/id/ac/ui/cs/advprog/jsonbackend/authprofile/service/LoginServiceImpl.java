package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(UserLoginRequest dto) {
        String dtoEmail = dto.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(dtoEmail);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + dtoEmail);
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }

        return user;
    }
}
