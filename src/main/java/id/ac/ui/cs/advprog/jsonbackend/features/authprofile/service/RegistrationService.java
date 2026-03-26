package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;

public interface RegistrationService {
    void register(UserRegistrationRequest dto);
}
