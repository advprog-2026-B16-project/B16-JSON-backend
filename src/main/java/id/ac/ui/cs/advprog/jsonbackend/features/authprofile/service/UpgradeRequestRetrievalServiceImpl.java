package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
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
        return upgradeRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        return upgradeRequestRepository.findByRequesterUser(user);
    }
}
