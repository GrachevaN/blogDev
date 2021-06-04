package main.repository;

import main.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository <Comment, Integer> {

}
