package id.ac.ui.cs.advprog.jsonbackend.authprofile.event;

public class UserCreatedEvent {

    private final String userId;

    public UserCreatedEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
