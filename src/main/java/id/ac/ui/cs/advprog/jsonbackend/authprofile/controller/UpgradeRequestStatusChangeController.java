package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.service.UpgradeRequestStatusChangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@RestController
@RequestMapping("api/upgrade-request")
public class UpgradeRequestStatusChangeController {

    UpgradeRequestStatusChangeService upgradeRequestStatusChangeService;

    @Autowired
    public UpgradeRequestStatusChangeController(UpgradeRequestStatusChangeService upgradeRequestStatusChangeService) {
        this.upgradeRequestStatusChangeService = upgradeRequestStatusChangeService;
    }

    @PatchMapping("change-status/{requestId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String requestId,
            @RequestBody UpgradeRequestStatusChangeRequest dto) {

        this.upgradeRequestStatusChangeService.updateRequestStatus(requestId, dto.getNewStatus());
        return ResponseEntity.ok("Status updated successfully");
    }
}
