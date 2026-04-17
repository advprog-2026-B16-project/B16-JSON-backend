package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;

import java.util.List;
import java.util.Optional;

public interface UpgradeRequestRetrievalService  {
    List<UpgradeRequest> getAllRequests();
    Optional<UpgradeRequest> getRequestByUsername(User user);
}
