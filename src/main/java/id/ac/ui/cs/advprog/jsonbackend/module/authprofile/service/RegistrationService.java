package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.dto.UserRegistrationRequest;

public interface RegistrationService {
    void register(UserRegistrationRequest dto);
}
