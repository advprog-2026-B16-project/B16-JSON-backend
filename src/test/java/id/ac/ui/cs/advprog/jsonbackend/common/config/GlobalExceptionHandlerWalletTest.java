package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InvalidAmountException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerWalletTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidAmountExceptionShouldReturnBadRequest() {
        ResponseEntity<ProblemDetail> response = handler.handleWalletException(new InvalidAmountException());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_AMOUNT", response.getBody().getTitle());
        assertEquals("INVALID_AMOUNT", response.getBody().getProperties().get("errorCode"));
    }

    @Test
    void handleInsufficientBalanceExceptionShouldReturnUnprocessableEntity() {
        ResponseEntity<ProblemDetail> response = handler.handleWalletException(new InsufficientBalanceException());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().getTitle());
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().getProperties().get("errorCode"));
    }

    @Test
    void handleWalletNotFoundExceptionShouldReturnNotFound() {
        ResponseEntity<ProblemDetail> response = handler.handleWalletException(new WalletNotFoundException("user1"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("WALLET_NOT_FOUND", response.getBody().getTitle());
        assertEquals("WALLET_NOT_FOUND", response.getBody().getProperties().get("errorCode"));
    }
}
