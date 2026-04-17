package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;

public interface LoginService {
    public User login(UserLoginRequest dto);
}
