package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeRequestSubmissionRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Credential URL or data is required")
    private String credential;
}
