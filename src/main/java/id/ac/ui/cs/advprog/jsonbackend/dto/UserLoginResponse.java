package id.ac.ui.cs.advprog.jsonbackend.dto;

import id.ac.ui.cs.advprog.jsonbackend.model.User;

public record UserLoginResponse (
    User user
) {

    public UserLoginResponse(User user) {
        this.user = user;
        this.user.setPassword("");
    }
}
