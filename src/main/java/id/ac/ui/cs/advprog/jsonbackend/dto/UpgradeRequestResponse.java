package id.ac.ui.cs.advprog.jsonbackend.dto;

import id.ac.ui.cs.advprog.jsonbackend.model.UpgradeRequest;

public record UpgradeRequestResponse (
        UpgradeRequest upgradeRequest
) {

    public UpgradeRequestResponse(UpgradeRequest upgradeRequest) {
        this.upgradeRequest = upgradeRequest;
    }
}
