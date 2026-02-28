//package id.ac.ui.cs.advprog.jsonbackend.service;
//
//import id.ac.ui.cs.advprog.jsonbackend.exception.UserNotFoundException;
//import id.ac.ui.cs.advprog.jsonbackend.model.UpgradeRequest;
//import id.ac.ui.cs.advprog.jsonbackend.model.User;
//import id.ac.ui.cs.advprog.jsonbackend.model.UserRole;
//import id.ac.ui.cs.advprog.jsonbackend.model.UserStatus;
//import id.ac.ui.cs.advprog.jsonbackend.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public class UpdateUserRoleServiceImpl implements UpdateUserRoleService{
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UpgradeRequestStatusChangeServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    @Transactional
//    public void updateRequestStatus(String username, String newStatus) {
//        Optional<User> optionalUser = userRepository.findByUsername(username);
//        if(optionalUser.isEmpty()) {
//            throw new UserNotFoundException("User not found for update status:" + username);
//        }
//        User user = optionalUser.get();
//        user.setStatus(UserStatus.ACTIVE);
//        user.setRole(UserRole.JASTIPER);
//        userRepository.save(user);
//        // implement UserStatusChange too here
//    }
//}
