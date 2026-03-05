package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {
    private final UpgradeRequestRepository upgradeRequestRepository;

    @Autowired
    public UpgradeRequestRetrievalServiceImpl(UpgradeRequestRepository upgradeRequestRepository) {
        this.upgradeRequestRepository = upgradeRequestRepository;
    }

    @Override
    public List<UpgradeRequest> getAllRequests() {
        return upgradeRequestRepository.findAll();
    }

    @Override
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        return upgradeRequestRepository.findByRequesterUser(user);
    }

}
