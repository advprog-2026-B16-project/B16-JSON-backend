package id.ac.ui.cs.advprog.jsonbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeRequestStatusChangeRequest {
    @NotBlank(message = "Password is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String newStatus;
}
