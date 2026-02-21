package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserRegistrationRequest;

public interface RegistrationService {
    void register(UserRegistrationRequest dto);
}
