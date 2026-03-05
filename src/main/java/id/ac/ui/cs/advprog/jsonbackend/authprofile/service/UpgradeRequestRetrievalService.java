package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;

import java.util.List;
import java.util.Optional;

public interface UpgradeRequestRetrievalService  {
    List<UpgradeRequest> getAllRequests();
    Optional<UpgradeRequest> getRequestByUsername(User user);
}
