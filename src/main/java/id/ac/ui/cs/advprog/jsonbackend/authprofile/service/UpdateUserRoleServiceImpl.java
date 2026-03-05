package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateUserRoleServiceImpl implements UpdateUserRoleService {
    private final UserRepository userRepository;

    @Autowired
    public UpdateUserRoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void updateUserRoleStatus(String requestId, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found for update status:" + username);
        }
        User user = optionalUser.get();
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.JASTIPER);
        userRepository.save(user);
    }
}
