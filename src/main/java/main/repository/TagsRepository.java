package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagsRepository extends JpaRepository <Tag, Integer> {

    @Query(value = "SELECT o from Tag o JOIN fetch Tag2Post t on o = t.tag")
    List<Tag> findTagsByPostsCount();


    @Query(value = "SELECT o from Tag o JOIN fetch Tag2Post t on o = t.tag where o.name LIKE :tagName%")
    List<Tag> findTagsByNameContaining(String tagName);

    Optional<Tag> findByName(String tag);


//    @Query(value = "SELECT o from Tag o JOIN fetch Tag2Post t on o = t.tag where o.name LIKE :name%")
//    List<Tag> findTagsByNameContaining(@Param("name") String name);
}
