package main.repository;

import main.model.CaptchaCodes;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository <Post, Integer> {



    @Query(value = "select o from Post o " +
            "join fetch Comment c on c.post = o " +
            "group by (c.post)" +
            "order by count (c) desc ")
    Page<Post> findAllByComment(Pageable pageable);


    @Query(value = "select p from Post p " +
            "join fetch Votes v on v.post = p " +
            "where (v.value = 1) " +
            "group by (v.post) order by count (p) desc")
    Page<Post> findAllByLikes(Pageable pageable);

    @Query(value = "select p from Post p order by (p.postTime) desc")
    Page<Post> findAllOrderByPostTimeDesc(Pageable pageable);

    @Query(value = "select p from Post p order by (p.postTime)")
    Page<Post> findAllOrderByPostTime(Pageable pageable);


    @Query(value = "select p from Post p where p.textContent LIKE %:queryText%")
    Page<Post> findAllByTextContent(Pageable pageable, String queryText);

    Page<Post> findByTitleContaining(Pageable pageable, String queryText);

    Page<Post> findByTextContentContaining(Pageable pageable, String queryText);

    @Query(value = "select p from Post p where p.postTime between :postTime and :stopTime")
//    WHERE datetime BETWEEN '2015-01-01' AND '2015-01-01 23:59:59'
    Page<Post> findPostByPostTimeContaining(Pageable pageable, Timestamp postTime, Timestamp stopTime);

//    Page<Post> findPostByPostTimeBefore(Pageable pageable, Timestamp time);

    @Query(value = "select p from Post p join fetch Tag2Post t on t.post = p where t.tag.name = :tagName")
    Page<Post> findAllByTag(Pageable pageable, String tagName);

    @Query(value = "select p from Post p where p.moderationStatus = :status and p.status = 1")
    Page<Post> findByModerationStatus(Pageable pageable, ModerationStatus status);

    @Query(value = "select p from Post p where p.moderationStatus = :status and p.moderator = :moder and p.status = 1")
    Page<Post> findByModerationStatusAndModerator(Pageable pageable, ModerationStatus status, User moder);

    @Query(value = "select p from Post p where p.moderationStatus = :status and p.user = :user and p.status = 1")
    Page<Post> findByModerationStatusAndUser(Pageable pageable, ModerationStatus status, User user);

    @Query(value = "select p from Post p where  p.user = :user and p.status = 0")
    Page<Post> findByStatusAndUser(Pageable pageable, User user);

    @Query(value = "select p from Post p where p.moderationStatus = :status")
    List<Post> findByModerationStatus(ModerationStatus status);

    List<Post> findAllByUserOrderByPostTime(User user);

    @Query(value = "select p from Post p order by p.postTime")
    List<Post> findAllPostByPostTime();
//    findAllOrderByPostTime();


}
