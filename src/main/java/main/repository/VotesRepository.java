package main.repository;

import main.model.Post;
import main.model.User;
import main.model.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotesRepository extends JpaRepository <Votes, Integer> {

    List<Votes> findAllByPostAndValue(Post post, byte value);

    List<Votes> findAllByValue(byte value);

    Optional<Votes> findByPostAndUser(Post post, User user);

}
