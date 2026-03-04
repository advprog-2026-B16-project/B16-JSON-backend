package id.ac.ui.cs.advprog.jsonbackend.wallet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.User;
import id.ac.ui.cs.advprog.jsonbackend.wallet.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.getAllUsers().get();
    }

}
