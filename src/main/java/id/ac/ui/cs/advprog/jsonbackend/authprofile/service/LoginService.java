package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;

public interface LoginService {
    public User login(UserLoginRequest dto);
}
