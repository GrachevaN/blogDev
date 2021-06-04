package main.repository;

import main.model.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotesRepository extends JpaRepository <Votes, Integer> {
}
