package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepo.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public void promoteToJastiper(User user) {
        user.setRole(UserRole.JASTIPER);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void banUser(UUID userId) {
        userRepo.findById(userId).ifPresent(user -> {
            if (user.getRole() != UserRole.ADMIN) {
                user.setStatus(UserStatus.BANNED);
                userRepo.save(user);
            }
        });
    }

    @Override
    @Transactional
    public void demoteUser(UUID userId) {
        userRepo.findById(userId).ifPresent(user -> {
            if (user.getRole() == UserRole.JASTIPER) {
                user.setRole(UserRole.TITIPER);
                userRepo.save(user);
            }
        });
    }
}
