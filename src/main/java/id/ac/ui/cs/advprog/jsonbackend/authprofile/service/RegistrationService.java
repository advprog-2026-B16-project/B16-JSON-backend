package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserRegistrationRequest;

public interface RegistrationService {
    void register(UserRegistrationRequest dto);
}
