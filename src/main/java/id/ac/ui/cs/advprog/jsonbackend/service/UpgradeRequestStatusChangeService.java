package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.dto.UpgradeRequestStatusChangeRequest;

import java.util.UUID;

public interface UpgradeRequestStatusChangeService {
    void updateRequestStatus(String requestId, String newStatus);
}
