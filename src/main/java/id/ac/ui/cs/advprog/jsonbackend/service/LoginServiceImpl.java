package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.exception.*;
import id.ac.ui.cs.advprog.jsonbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new WrongPasswordException(String.format("Wrong password true: %s input: %s", dto.getPassword(), user.getPassword()));
        }

        return user;
    }
}
