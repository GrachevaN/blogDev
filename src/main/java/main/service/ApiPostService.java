package main.service;

import main.DTO.CommentDTO;
import main.DTO.ErrorsDTO;
import main.DTO.PostDTO;
import main.DTO.UserDTO;
import main.api.errs.DocumentNotFoundExc;
import main.api.response.AddingNewResponse;
import main.api.response.ApiPostResponse;
import main.model.*;
import main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.sql.Timestamp;
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

    @Autowired
    private TagsRepository tagsRepository;

    private AuthCheckService authCheckService;

    public ApiPostService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public ApiPostResponse getApiPostResponse(Pageable pageable, String mode) {
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
        return apiPostResponse;
    }

//    public ApiPostResponse searchPostResponse(Pageable pageable, String theQuery) {
    public ApiPostResponse searchPostResponse(Pageable pageable, String theQuery) {
        List<Post> posts = new ArrayList<>();
        theQuery.trim();
        if (theQuery.isEmpty()) {
            theQuery = "recent";
        }

        switch (theQuery) {
            case ("recent"):
                posts = postRepository.findAllOrderByPostTime(pageable).getContent();
                break;
            default:
                posts.addAll(postRepository.findByTitleContaining(pageable, theQuery).getContent());
                posts.addAll(postRepository.findByTextContentContaining(pageable, theQuery).getContent());
                break;
        }
        ApiPostResponse apiPostResponse = makePostResponse(posts);


        return apiPostResponse;
    }

    public PostDTO getPostById (int id, Principal principal) {
        User authUser =  authCheckService.getAuthUser(principal);
        PostDTO postDTO = new PostDTO();
        Calendar calendar = Calendar.getInstance();
        Post post = postRepository.findById(id).orElseThrow(() -> new DocumentNotFoundExc());
        if (
                (post.getStatus()==1)
                && post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                && post.getPostTime().before((calendar.getTime()))
        )
        {
            UserDTO user = new UserDTO(post.getUser().getId(), post.getUser().getName());
            postDTO.setId(post.getId());
//            calendar.setTimeInMillis(post.getPostTime().getTime());
//            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//            postDTO.setTimestamp(calendar.getTimeInMillis());
            postDTO.setTimestamp(post.getPostTime().getTime());
            postDTO.setUser(user);
            postDTO.setTitle(post.getTitle());
            postDTO.setText(post.getTextContent());
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
//            postDTO.setAnnounce(announceCreate(post.getTextContent()));


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

                if (authUser.getIs_moderator() != 1 && !authUser.equals(post.getUser())) {
                    post.setViewCount(post.getViewCount() + 1);
                    postDTO.setViewCount(post.getViewCount());
                    postRepository.save(post);
                }
//                else {
//                    post.setViewCount(post.getViewCount() + 1);
//                    postDTO.setViewCount(post.getViewCount());
//                    postRepository.save(post);
//                }
        }
        return postDTO;
    }

    public ApiPostResponse getPostByDate (Pageable pageable, String theDate) {

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

        return apiPostResponse;
    }

    public ApiPostResponse getPostsByTag (Pageable pageable, String tag) {

        ApiPostResponse apiPostResponse = new ApiPostResponse();

        if (tag.isEmpty()) {
            return apiPostResponse;
        }
        else {
            List<Post> posts = postRepository.findAllByTag(pageable, tag).getContent();
            apiPostResponse = makePostResponse(posts);

            return apiPostResponse;
        }


    }

    public ApiPostResponse getModeratorsPosts (Pageable pageable, String status, Principal principal) {
        User user = authCheckService.getAuthUser(principal);
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        if (!user.equals(null)) {
            List<Post> posts = new ArrayList<>();
            switch (status) {
                case "new":
                    posts = postRepository.findByModerationStatus(pageable, ModerationStatus.NEW).getContent();
                    break;
                case "declined":
                    posts = postRepository.findByModerationStatusAndModerator(pageable, ModerationStatus.DECLINED, user).getContent();
                    break;
                case "accepted":
                    posts = postRepository.findByModerationStatusAndModerator(pageable, ModerationStatus.ACCEPTED, user).getContent();
                    break;
            }
//            posts = posts.stream().filter(x -> x.getStatus()==1)
//                    .collect(Collectors.toList());

            posts.forEach(
                    post -> {
                        PostDTO postDTO = makePostDTO(post);
                        apiPostResponse.increaseCount();
                        apiPostResponse.addPost(postDTO);
                    }
            );
        }
        return apiPostResponse;
    }

    public ApiPostResponse getMyPosts (Pageable pageable, String status, Principal principal) {
        User user = authCheckService.getAuthUser(principal);
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        if (!user.equals(null)) {
            List<Post> posts = new ArrayList<>();
            switch (status) {
                case "inactive":
                    posts = postRepository.findByStatusAndUser(pageable, user).getContent();
                    break;
                case "pending":
                    posts = postRepository.findByModerationStatusAndUser(pageable, ModerationStatus.NEW, user).getContent();
                    break;
                case "declined":
                    posts = postRepository.findByModerationStatusAndUser(pageable, ModerationStatus.DECLINED, user).getContent();
                    break;
                case "published":
                    posts = postRepository.findByModerationStatusAndUser(pageable, ModerationStatus.ACCEPTED, user).getContent();
                    break;
            }
            posts.forEach(
                    post -> {
                        PostDTO postDTO = makePostDTO(post);
                        apiPostResponse.increaseCount();
                        apiPostResponse.addPost(postDTO);
                    }
            );

        }
        return apiPostResponse;
    }

    public AddingNewResponse addPost(Principal principal, Timestamp timestamp, byte active, String title, String text,
                                     List<String> tags) {
        User user = authCheckService.getAuthUser(principal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        if (!user.equals(null)) {
            ErrorsDTO errorsDTO = new ErrorsDTO();
            Calendar calendar = Calendar.getInstance();
            int textLength = 50;
            int titleLength = 3;
            if (title.isEmpty() || title.length() < titleLength) {
                errorsDTO.setTitleError();
                addingNewResponse.setResult(false);
            }
            if (text.length() < textLength) {
                errorsDTO.setTextError();
                addingNewResponse.setResult(false);
            }
            if (addingNewResponse.isResult()) {
                if (timestamp.before(calendar.getTime())) {
                    timestamp.setTime(calendar.getTime().getTime());
                }
                Post post = new Post();
                post.setPostTime(timestamp);
                post.setStatus(active);
                post.setTitle(title);
                post.setTextContent(text);
                post.setUser(user);
                postRepository.save(post);

                if (!tags.isEmpty()) {
                    tags.forEach(s -> {
                        Tag tag = tagsRepository.findByName(s).orElseThrow();
                        Tag2Post tag2Post = new Tag2Post();
                        tag2Post.setPost(post);
                        tag2Post.setTag(tag);
                        tag2PostRepository.save(tag2Post);
                    });
                }
            }
        }
        return addingNewResponse;
    }

    private ApiPostResponse makePostResponse (List<Post> posts) {
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        Calendar calendar = Calendar.getInstance();
        posts = posts.stream().filter(x -> x.getStatus()==1
                && x.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                && x.getPostTime().before((calendar.getTime())))
                .collect(Collectors.toList());
        posts.forEach(
                post -> {
                    calendar.setTimeInMillis(post.getPostTime().getTime());
                    PostDTO postDTO = makePostDTO(post);
                    apiPostResponse.increaseCount();
                    apiPostResponse.addPost(postDTO);
                }
        );
        return apiPostResponse;
    }

    private PostDTO makePostDTO (Post post) {

        UserDTO user = new UserDTO(post.getUser().getId(), post.getUser().getName());
        PostDTO postDTO = new PostDTO(post.getId(), post.getPostTime(),
                user, post.getTitle());
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
