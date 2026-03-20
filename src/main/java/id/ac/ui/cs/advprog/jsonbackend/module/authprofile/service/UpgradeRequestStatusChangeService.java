package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.service;

public interface UpgradeRequestStatusChangeService {
    void updateRequestStatus(String requestId, String newStatus);
}
