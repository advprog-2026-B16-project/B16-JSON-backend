package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import lombok.Builder;
import java.util.UUID;
@Builder
public record UserLoginResponse (UUID id, String username, String email, String role, String status, String token) {
    public static UserLoginResponse fromUser(User u, String token) {
        return UserLoginResponse.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail()).role(u.getRole().name()).status(u.getStatus().name()).token(token).build();
    }
}