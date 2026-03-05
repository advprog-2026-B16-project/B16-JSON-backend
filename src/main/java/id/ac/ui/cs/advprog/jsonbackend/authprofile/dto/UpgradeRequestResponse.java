package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;

public record UpgradeRequestResponse (
        UpgradeRequest upgradeRequest
) {

    public UpgradeRequestResponse(UpgradeRequest upgradeRequest) {
        this.upgradeRequest = upgradeRequest;
    }
}
