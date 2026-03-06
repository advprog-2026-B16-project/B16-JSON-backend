package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

public interface UpgradeRequestStatusChangeService {
    void updateRequestStatus(String requestId, String newStatus);
}
