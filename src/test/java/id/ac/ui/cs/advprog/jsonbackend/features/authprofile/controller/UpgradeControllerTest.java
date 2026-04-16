package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;

class UpgradeControllerTest {

    private final UUID requestId = UUID.randomUUID();

    @Mock
    private UpgradeRequestRetrievalService retrievalService;

    @Mock
    private UpgradeRequestStatusChangeService statusChangeService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

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
    void testUpdateStatusSuccess() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setNewStatus("ACCEPTED");
        request.setUsername("testuser");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn("admin");

        ResponseEntity<?> response = statusChangeController.updateStatus(requestId, request, bindingResult, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSubmitRequestSuccess() {
        UpgradeRequestSubmissionRequest request = new UpgradeRequestSubmissionRequest();
        request.setFullName("Test User");
        request.setCredential("Credential");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(java.util.Optional.of(new User()));

        ResponseEntity<?> response = statusChangeController.submitRequest(request, bindingResult, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAllBranchesSubmitRequest() throws Exception {
        java.lang.reflect.Field verboseField = UpgradeRequestStatusChangeController.class.getDeclaredField("verboseLogging");
        verboseField.setAccessible(true);

        UpgradeRequestSubmissionRequest dto = new UpgradeRequestSubmissionRequest("Name", "Cred");
        BindingResult result = mock(BindingResult.class);
        
        // 1. Verbose=T, hasErrors=F, Success
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(java.util.Optional.of(new User()));
        statusChangeController.submitRequest(dto, result, authentication);

        // 2. Verbose=F, hasErrors=F, Success
        verboseField.set(statusChangeController, false);
        statusChangeController.submitRequest(dto, result, authentication);

        // 3. Verbose=T, hasErrors=T
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("dto", "fullName", "err")));
        statusChangeController.submitRequest(dto, result, authentication);

        // 4. Verbose=F, hasErrors=T
        verboseField.set(statusChangeController, false);
        statusChangeController.submitRequest(dto, result, authentication);

        // 5. Verbose=T, Catch Block (User NotFound via lambda)
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(false);
        when(userService.getUserByUsername("user")).thenReturn(java.util.Optional.empty());
        statusChangeController.submitRequest(dto, result, authentication);

        // 6. Verbose=F, Catch Block (User NotFound via lambda)
        verboseField.set(statusChangeController, false);
        statusChangeController.submitRequest(dto, result, authentication);
        
        // 7. Verbose=T, Catch Block (Exception)
        verboseField.set(statusChangeController, true);
        when(userService.getUserByUsername("user")).thenThrow(new RuntimeException("Err"));
        statusChangeController.submitRequest(dto, result, authentication);
        
        // 8. Verbose=F, Catch Block (Exception)
        verboseField.set(statusChangeController, false);
        statusChangeController.submitRequest(dto, result, authentication);
    }

    @Test
    void testAllBranchesUpdateStatus() throws Exception {
        java.lang.reflect.Field verboseField = UpgradeRequestStatusChangeController.class.getDeclaredField("verboseLogging");
        verboseField.setAccessible(true);

        UpgradeRequestStatusChangeRequest dto = new UpgradeRequestStatusChangeRequest();
        dto.setNewStatus("ACCEPTED");
        BindingResult result = mock(BindingResult.class);
        
        // 1. Verbose=T, hasErrors=F, Success
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn("admin");
        statusChangeController.updateStatus(requestId, dto, result, authentication);

        // 2. Verbose=F, hasErrors=F, Success
        verboseField.set(statusChangeController, false);
        statusChangeController.updateStatus(requestId, dto, result, authentication);

        // 3. Verbose=T, hasErrors=T
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("dto", "status", "err")));
        statusChangeController.updateStatus(requestId, dto, result, authentication);

        // 4. Verbose=F, hasErrors=T
        verboseField.set(statusChangeController, false);
        statusChangeController.updateStatus(requestId, dto, result, authentication);

        // 5. Verbose=T, Catch Block
        verboseField.set(statusChangeController, true);
        when(result.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Err")).when(statusChangeService).updateRequestStatus(any(), any());
        statusChangeController.updateStatus(requestId, dto, result, authentication);

        // 6. Verbose=F, Catch Block
        verboseField.set(statusChangeController, false);
        statusChangeController.updateStatus(requestId, dto, result, authentication);
    }

    @Test
    void testRetrievalControllerVerbose() throws Exception {
        java.lang.reflect.Field field = UpgradeRequestRetrievalController.class.getDeclaredField("verboseLogging");
        field.setAccessible(true);
        field.set(retrievalController, true);
        when(retrievalService.getAllRequests()).thenReturn(new ArrayList<>());
        retrievalController.getAllRequests();
        
        field.set(retrievalController, false);
        retrievalController.getAllRequests();
    }
}
