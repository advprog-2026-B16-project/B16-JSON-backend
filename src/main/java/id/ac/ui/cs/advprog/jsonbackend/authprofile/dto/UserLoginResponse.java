package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import lombok.Builder;
import java.util.UUID;

@Builder
public record UserLoginResponse (
    UUID id,
    String username,
    String email,
    String role,
    String status,
    String token
) {
    public static UserLoginResponse fromUser(User user, String token) {
        return UserLoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .token(token)
                .build();
    }
}
