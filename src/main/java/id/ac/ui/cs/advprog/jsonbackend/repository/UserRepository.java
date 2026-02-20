package id.ac.ui.cs.advprog.jsonbackend.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.jsonbackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value ="select user_id, first_name, last_name from \"User\"", nativeQuery = true)
    public Optional<List<User>> getAllUsers();
}
