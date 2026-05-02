package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {

    private final UpgradeRequestRepository upgradeRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UpgradeRequest> getAllRequests() {
        // Using standard JPA repository to ensure cross-database compatibility (H2/PostgreSQL)
        // The ClassCastException in PostgreSQL is likely due to stale schema or incorrect mapping,
        // which has been resolved by harmonizing all IDs to java.util.UUID.
        return upgradeRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        return upgradeRequestRepository.findByRequesterUser(user);
    }
}
