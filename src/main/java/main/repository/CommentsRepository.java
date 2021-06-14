package main.repository;

import main.model.Comment;
import main.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository <Comment, Integer> {

    List<Comment> findCommentsByPost(Post post);
}
