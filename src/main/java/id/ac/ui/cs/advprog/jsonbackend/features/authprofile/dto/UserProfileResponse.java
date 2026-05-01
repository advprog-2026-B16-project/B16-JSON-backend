package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private String status;
    private String fullName;
    private String bio;
    private String location;
    private String avatarUrl;
    private Long successfulTransactions;
}
