package main.repository;

import main.model.CaptchaCodes;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository <Post, Integer> {



    @Query(value = "select p from Post p " +
            "left join fetch Comment c on c.post = p " +
            "where p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus " +
            "group by (p.id) order by count (c) desc ")
    Page<Post> findAllByComment(Pageable pageable, Timestamp currentTime, ModerationStatus moderationStatus);


    @Query(value = "select p from Post p left join fetch Votes v on v.post = p " +
            "where  p.status = 1 and p.postTime < :currentTime and p.moderationStatus = :moderationStatus " +
            "group by (p.id) " +
            "order by count(v.value) desc")
    Page<Post> findAllByLikes(Pageable pageable, @Param("currentTime") Timestamp currentTime, @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "select p from Post p where p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus order by (p.postTime) desc")
    Page<Post> findAllOrderByPostTimeDesc(Pageable pageable, Timestamp currentTime, ModerationStatus moderationStatus);

    @Query(value = "select p from Post p where p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus order by (p.postTime)")
    Page<Post> findAllOrderByPostTime(Pageable pageable, Timestamp currentTime, ModerationStatus moderationStatus);


    @Query(value = "select p from Post p where p.textContent LIKE %:queryText% and p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus")
    Page<Post> findAllByTextContent(Pageable pageable, String queryText, Timestamp currentTime, ModerationStatus moderationStatus);

    @Query(value = "select p from Post p where p.title LIKE %:queryText% and p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus")
    Page<Post> findByTitleContaining(Pageable pageable, String queryText, Timestamp currentTime, ModerationStatus moderationStatus);

    @Query(value = "select p from Post p where p.postTime between :postTime and :stopTime and p.status = 1 and p.moderationStatus = :moderationStatus")
    Page<Post> findPostByPostTimeContaining(Pageable pageable, Timestamp postTime, Timestamp stopTime, ModerationStatus moderationStatus);

    @Query(value = "select p from Post p join fetch Tag2Post t on t.post = p where t.tag.name = :tagName and p.postTime < :currentTime and p.status = 1 and p.moderationStatus = :moderationStatus")
    Page<Post> findAllByTag(Pageable pageable, String tagName, Timestamp currentTime, ModerationStatus moderationStatus);

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


}
