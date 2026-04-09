package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

public interface UpgradeRequestStatusChangeService {
    void updateRequestStatus(String requestId, String newStatus);
}
