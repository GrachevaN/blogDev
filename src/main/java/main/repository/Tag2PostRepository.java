package main.repository;

import main.model.Tag;
import main.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Tag2PostRepository extends JpaRepository <Tag2Post, Integer> {

    @Query(value = "SELECT o from Tag2Post o JOIN Tag t on o.tag = t where t.name LIKE 'tagName%'")
    List<Tag2Post> findTagsByNameContaining(@Param("id") String tagName);

    @Query(value = "SELECT o from Tag2Post o order by count (o.post)")
    List<Tag2Post> findAllByPostsCount();


}
