package main.service;

import main.DTO.CommentDTO;
import main.DTO.PostDTO;
import main.DTO.UserDTO;
import main.api.response.ApiPostResponse;
import main.model.*;
import main.repository.CommentsRepository;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.VotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ApiPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;


    public ResponseEntity<?> getApiPostResponse(Pageable pageable, String mode) {
        List<Post> posts = new ArrayList<>();

        switch (mode) {
            case ("recent"):
                posts = postRepository.findAllOrderByPostTime(pageable).getContent();
                break;
            case ("popular"):
                posts = postRepository.findAllByComment(pageable).getContent();
                break;
            case ("best"):
                posts = postRepository.findAllByLikes(pageable).getContent();
                break;
            case ("early"):
                posts = postRepository.findAllOrderByPostTimeDesc(pageable).getContent();
                break;
        }

        ApiPostResponse apiPostResponse = makePostResponse(posts);

        if (apiPostResponse.getCount() == 0) {
            return new ResponseEntity<>(apiPostResponse, HttpStatus.resolve(200));
        }

        return new ResponseEntity<>(apiPostResponse, HttpStatus.OK);
    }

//    public ApiPostResponse searchPostResponse(Pageable pageable, String theQuery) {
    public ResponseEntity<?> searchPostResponse(Pageable pageable, String theQuery) {
        List<Post> posts = new ArrayList<>();

        switch (theQuery) {
            case ("recent"):
                posts = postRepository.findAllOrderByPostTime(pageable).getContent();
                break;
            default:
                posts = postRepository.findByTitleContaining(pageable, theQuery).getContent();
                posts.addAll(postRepository.findByTextContentContaining(pageable, theQuery).getContent());
                break;
        }
        ApiPostResponse apiPostResponse = makePostResponse(posts);

        if (apiPostResponse.getCount() == 0) {
            return new ResponseEntity<>(apiPostResponse, HttpStatus.resolve(200));
        }

        return new ResponseEntity<>(apiPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostById (int id, HttpSession session) {
        User authUser = AuthCheckService.getAuthUser(session);

        Calendar calendar = Calendar.getInstance();
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if ((post.getStatus()==1 || authUser != null)
                    && post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                    && post.getPostTime().before((calendar.getTime())))
            {
                UserDTO user = new UserDTO(post.getUser().getId(), post.getUser().getName());
                PostDTO postDTO = new PostDTO(post.getId(), post.getPostTime()
                        , user, post.getTitle());
                postDTO.setLikeCount(
                        votesRepository.findAll().stream().filter(vote ->
                                vote.getPost().equals(post)
                                        && vote.getValue() == 1
                        ).count()
                );
                postDTO.setDislikeCount(
                        votesRepository.findAll().stream().filter(vote ->
                                vote.getPost().equals(post)
                                        && vote.getValue() == -1
                        ).count()
                );
                postDTO.setAnnounce(announceCreate(post.getTextContent()));


                List<Comment> comments = commentsRepository.findCommentsByPost(post);
                List<CommentDTO> commentDTOList = new ArrayList<>();

                comments.forEach(comment -> {
                    CommentDTO commentDTO = new CommentDTO();
                    commentDTO.setId(comment.getId());
                    commentDTO.setText(comment.getText());
                    commentDTO.setTimestamp(comment.getCommentPostTime().getTime());
                    UserDTO userDTO = new UserDTO(comment.getUser().getId(), comment.getUser().getName());
                    userDTO.setPhoto(comment.getUser().getPhoto());
                    commentDTO.setUser(userDTO);
                    commentDTOList.add(commentDTO);
                });

                if (!commentDTOList.isEmpty()) {
                    postDTO.setComments(commentDTOList);
                }


                List<Tag2Post> tags = tag2PostRepository.findByPost(post);
                List<String> tagsList = new ArrayList<>();
                tags.forEach(tag2Post -> {
                    tagsList.add(tag2Post.getTag().getName());
                });
                if (!tagsList.isEmpty()) {
                    postDTO.setTags(tagsList);
                }

                if (authUser != null) {
                    if (authUser.getIs_moderator() != 1 && !authUser.equals(post.getUser())) {
                        post.setViewCount(post.getViewCount() + 1);
                        postDTO.setViewCount(post.getViewCount());
                        postRepository.save(post);
                    }
                }
                else {
                    post.setViewCount(post.getViewCount() + 1);
                    postDTO.setViewCount(post.getViewCount());
                    postRepository.save(post);
                }

                return new ResponseEntity<>(postDTO, HttpStatus.OK);
            }

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public ResponseEntity<?> getPostByDate (Pageable pageable, String theDate) {

        String[] args = theDate.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Timestamp stopTimestamp = new Timestamp(calendar.getTime().getTime());
        List<Post> posts = postRepository.findPostByPostTimeContaining(pageable, timestamp, stopTimestamp).getContent();
        ApiPostResponse apiPostResponse = makePostResponse(posts);

        if (apiPostResponse.getCount() == 0) {
            return new ResponseEntity<>(apiPostResponse, HttpStatus.resolve(200));
        }

        return new ResponseEntity<>(apiPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostsByTag (Pageable pageable, String tag) {

        ApiPostResponse apiPostResponse = new ApiPostResponse();

        if (tag.isEmpty()) {
            return new ResponseEntity<>(apiPostResponse, HttpStatus.resolve(200));
        }
        else {
            List<Post> posts = postRepository.findAllByTag(pageable, tag).getContent();
            apiPostResponse = makePostResponse(posts);
            if (apiPostResponse.getCount() == 0) {
                return new ResponseEntity<>(apiPostResponse, HttpStatus.resolve(200));
            }
            return new ResponseEntity<>(apiPostResponse, HttpStatus.OK);
        }


    }



    private ApiPostResponse makePostResponse (List<Post> posts) {
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        Calendar calendar = Calendar.getInstance();
        posts = posts.stream().filter(x -> x.getStatus()==1
                && x.getModerationStatus().equals(ModerationStatus.NEW)
                && x.getPostTime().before((calendar.getTime())))
                .collect(Collectors.toList());

        posts.forEach(
                post -> {
                    PostDTO postDTO = makePostDTO(post);
                    apiPostResponse.increaseCount();
                    apiPostResponse.addPost(postDTO);
                }
        );
        return apiPostResponse;
    }

    private PostDTO makePostDTO (Post post) {

        UserDTO user = new UserDTO(post.getUser().getId(), post.getUser().getName());
        PostDTO postDTO = new PostDTO(post.getId(), post.getPostTime()
                , user, post.getTitle());
        postDTO.setCommentCount(
                commentsRepository.findAll().stream().filter(comment ->
                        comment.getPost().equals(post)
                ).count()
        );
        postDTO.setLikeCount(
                votesRepository.findAll().stream().filter(vote ->
                        vote.getPost().equals(post)
                                && vote.getValue() == 1
                ).count()
        );
        postDTO.setDislikeCount(
                votesRepository.findAll().stream().filter(vote ->
                        vote.getPost().equals(post)
                                && vote.getValue() == -1
                ).count()
        );
        postDTO.setViewCount(post.getViewCount());
        postDTO.setAnnounce(announceCreate(post.getTextContent()));
        return postDTO;

    }

    public static String announceCreate (String text) {
        StringBuilder builder = new StringBuilder();
        text = text.replaceAll("<[^>]+>", "");
        for (String word: text.split(" ") ) {
            if (builder.length() + (word + " ").length() < 150) {
                builder.append(word + " ");
            }
            else {
                break;
            }
        }
        builder.append("...");
        return builder.toString();
    }

}
