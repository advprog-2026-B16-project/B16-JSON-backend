package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.*;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;

    @Autowired
    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(UserLoginRequest dto) {
        User user;
        String dtoEmail = dto.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(dtoEmail);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + dtoEmail);
        }
        else {
            user = optionalUser.get();
        }

        if (!dto.getPassword().matches(user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }

        return user;
    }
}
