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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testGetAllRequests() {
        when(retrievalService.getAllRequests()).thenReturn(new ArrayList<>());
        ResponseEntity<?> response = retrievalController.getAllRequests();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetRequestByUsername() {
        id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User user = new id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User();
        when(retrievalService.getRequestByUsername(user)).thenReturn(java.util.Optional.empty());
        java.util.Optional<?> response = retrievalService.getRequestByUsername(user);
        assertEquals(java.util.Optional.empty(), response);
    }

    @Test
    void testUpdateStatusSuccess() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setNewStatus("ACCEPTED");
        request.setUsername("testuser");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = statusChangeController.updateStatus("req123", request, bindingResult);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateStatusBindingErrors() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("request", "newStatus", "Required")));

        ResponseEntity<?> response = statusChangeController.updateStatus("req123", request, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateStatusException() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setNewStatus("ACCEPTED");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error")).when(statusChangeService).updateRequestStatus(anyString(), anyString());

        ResponseEntity<?> response = statusChangeController.updateStatus("req123", request, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
