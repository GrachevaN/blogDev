package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Integer> {

    @Query(value = "select u from User u where u.email = :email")
    Optional<User> findByEmail(String email);

}
