package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<User> getUserById(java.util.UUID id) {
        return userRepo.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void promoteToJastiper(User user) {
        user.setRole(id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole.JASTIPER);
        userRepo.save(user);
    }
}
