package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;

public interface LoginService {
    public User login(UserLoginRequest dto);
}
