package id.ac.ui.cs.advprog.jsonbackend.handler;

import id.ac.ui.cs.advprog.jsonbackend.exception.WalletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ErrorResponse> handleWalletException(WalletException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return null; // TODO
            }

            @Override
            public ProblemDetail getBody() {
                return null; // TODO
            }
        };
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Something went wrong"
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public ProblemDetail getBody() {
                return null; // TODO
            }
        };
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                message
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return null; // TODO
            }

            @Override
            public ProblemDetail getBody() {
                return null; // TODO
            }
        };

        return ResponseEntity.badRequest().body(response);
    }
}
