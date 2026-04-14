package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeRequestStatusChangeRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "New status is required")
    private String newStatus;
}
