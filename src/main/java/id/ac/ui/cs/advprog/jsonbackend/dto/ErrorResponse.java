package id.ac.ui.cs.advprog.jsonbackend.dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // getter
}