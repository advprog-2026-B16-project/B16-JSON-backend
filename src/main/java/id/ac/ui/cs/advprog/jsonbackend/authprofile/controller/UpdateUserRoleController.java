package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpdateUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/upgrade-request")
public class UpdateUserRoleController {

    private final UpdateUserRoleService updateUserRoleService;

    @Autowired
    public UpdateUserRoleController(UpdateUserRoleService updateUserRoleService) {
        this.updateUserRoleService = updateUserRoleService;
    }

    @PatchMapping("change-status/{requestId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String requestId,
            @RequestBody UpgradeRequestStatusChangeRequest dto) {

        this.updateUserRoleService.updateUserRoleStatus(requestId, dto.getUsername());
        return ResponseEntity.ok("Status updated successfully");
    }
}
