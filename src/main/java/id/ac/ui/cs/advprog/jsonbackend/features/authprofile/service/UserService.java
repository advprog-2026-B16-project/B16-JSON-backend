package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(UUID id);
    User saveUser(User user);
    void promoteToJastiper(User user);
    void banUser(UUID userId);
    void demoteUser(UUID userId);
}
