package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;

public record UserLoginResponse (
    User user
) {

    public UserLoginResponse(User user) {
        this.user = user;
        this.user.setPassword("");
    }
}
