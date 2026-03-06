package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeRequestRegistrationRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Credential information is required")
    private String credential;
}
