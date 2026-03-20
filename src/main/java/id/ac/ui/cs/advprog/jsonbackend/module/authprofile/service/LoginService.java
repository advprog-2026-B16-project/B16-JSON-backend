package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.User;

public interface LoginService {
    public User login(UserLoginRequest dto);
}
