package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService{

    private final UpgradeRequestRepository upgradeRequestRepository;

    @Autowired
    public UpgradeRequestStatusChangeServiceImpl(UpgradeRequestRepository upgradeRequestRepository) {
        this.upgradeRequestRepository = upgradeRequestRepository;
    }

    @Override
    @Transactional
    public void updateRequestStatus(String requestId, String newStatus) {
        UpgradeRequest request = upgradeRequestRepository.getReferenceById(UUID.fromString(requestId));
        request.setStatus(newStatus);
        upgradeRequestRepository.save(request);
        // implement UserStatusChange too here
    }

//    private void applyAcceptance(UpgradeRequest request) {
//        request.setStatus("ACCEPTED");
//        User user = request.getRequesterUser();
//        user.setRole(UserRole.JASTIPER); // Execute the role promotion
//        userRepository.save(user);
//    }
}
