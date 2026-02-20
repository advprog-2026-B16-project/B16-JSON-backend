package id.ac.ui.cs.advprog.jsonbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.service.UserServiceImpl;

@RestController
@RequestMapping("api/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers() {

        return new ResponseEntity<>(this.userService.getAllUsers(), HttpStatus.OK);
    }


}