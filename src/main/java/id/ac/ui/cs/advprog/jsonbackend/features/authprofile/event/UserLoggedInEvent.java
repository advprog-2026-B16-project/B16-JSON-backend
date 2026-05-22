package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event;

public class UserLoggedInEvent {

    private final String userId;

    public UserLoggedInEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
