package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@RestController @RequestMapping("api/user") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    @GetMapping("/getUsers") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<List<UserLoginResponse>> getUsers() { return new ResponseEntity<>(userService.getAllUsers().stream().map(u -> UserLoginResponse.fromUser(u, null)).collect(Collectors.toList()), HttpStatus.OK); }
    @PatchMapping("/{id}/ban") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> banUser(@PathVariable UUID id) { userService.banUser(id); return ResponseEntity.ok().build(); }
    @PatchMapping("/{id}/demote") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> demoteUser(@PathVariable UUID id) { userService.demoteUser(id); return ResponseEntity.ok().build(); }
    @PutMapping("/profile") public ResponseEntity<Void> updateProfile(@RequestBody UserProfileUpdateRequest request, Principal principal) { userService.getUserByUsername(principal.getName()).ifPresent(u -> userService.updateProfile(u.getId(), request)); return ResponseEntity.ok().build(); }
    @GetMapping("/profile") public ResponseEntity<UserProfileResponse> getProfile(Principal p) { return userService.getUserByUsername(p.getName()).map(this::convertToProfileResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @GetMapping("/profile/{username}") public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable String username) { return userService.getUserByUsername(username).map(this::convertToProfileResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    private UserProfileResponse convertToProfileResponse(User u) {
        Long stats = u.getRole() == UserRole.JASTIPER ? orderService.getOrderByJastiperId(u.getId().toString()).stream().filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED).count() : null;
        return UserProfileResponse.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail()).role(u.getRole().name()).status(u.getStatus().name()).fullName(u.getFullName()).bio(u.getBio()).location(u.getLocation()).avatarUrl(u.getAvatarUrl()).successfulTransactions(stats).build();
    }
}