package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository <Post, Integer> {


//    select posts.id
//#, count(post_id) as posts_count
//    from posts
//    join post_comments on posts.id = post_comments.post_id
//    group by posts.id
//    order by count(post_id) desc

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

//    Page<Post> findAllOrderByPostTime(Pageable pageable);


}
