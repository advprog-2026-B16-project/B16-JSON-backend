package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestStatusChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpgradeControllerTest {

    @Mock
    private UpgradeRequestRetrievalService retrievalService;

    @Mock
    private UpgradeRequestStatusChangeService statusChangeService;

    @InjectMocks
    private UpgradeRequestRetrievalController retrievalController;

    @InjectMocks
    private UpgradeRequestStatusChangeController statusChangeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRequests() {
        when(retrievalService.getAllRequests()).thenReturn(List.of());
        ResponseEntity<?> response = retrievalController.getUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(retrievalService).getAllRequests();
    }

    @Test
    void testUpdateStatus() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setNewStatus("APPROVED");
        
        ResponseEntity<?> response = statusChangeController.updateStatus("req123", request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(statusChangeService).updateRequestStatus("req123", "APPROVED");
    }
}
