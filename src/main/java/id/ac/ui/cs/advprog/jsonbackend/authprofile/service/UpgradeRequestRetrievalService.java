package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;

import java.util.List;
import java.util.Optional;

public interface UpgradeRequestRetrievalService  {
    List<UpgradeRequest> getAllRequests();
    Optional<UpgradeRequest> getRequestByUsername(User user);
}
