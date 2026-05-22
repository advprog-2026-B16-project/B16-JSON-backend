package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String bio;
    private String location;
    private String avatarUrl;
}
