package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.service;

import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.User;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
}
