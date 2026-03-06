package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import lombok.Builder;

@Builder
public record UserLoginResponse (
    String id,
    String username,
    String email,
    String role,
    String status,
    String token
) {
    public static UserLoginResponse fromUser(User user, String token) {
        return UserLoginResponse.builder()
                .id(user.getUsername())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .token(token)
                .build();
    }
}
