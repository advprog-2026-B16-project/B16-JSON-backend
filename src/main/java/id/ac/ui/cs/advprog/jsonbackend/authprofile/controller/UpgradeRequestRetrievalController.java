package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestRetrievalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/upgrade-request")
public class UpgradeRequestRetrievalController {

    UpgradeRequestRetrievalService upgradeRequestRetrievalService;

    @Autowired
    public UpgradeRequestRetrievalController(UpgradeRequestRetrievalService upgradeRequestRetrievalService) {
        this.upgradeRequestRetrievalService = upgradeRequestRetrievalService;
    }
    
    @GetMapping("/get-requests")
    public ResponseEntity<?> getUsers() {

        return new ResponseEntity<>(this.upgradeRequestRetrievalService.getAllRequests(), HttpStatus.OK);
    }
}
