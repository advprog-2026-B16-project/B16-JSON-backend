package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import java.util.UUID;
public interface UpgradeRequestStatusChangeService {
    UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest requestDto);
    void updateRequestStatus(UUID requestId, String newStatus);
}