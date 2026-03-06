package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;

public interface UpgradeRequestService {
    void createRequest(User user, UpgradeRequestRegistrationRequest dto);
}
