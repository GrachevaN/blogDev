package main.service;

import main.dto.CommentDTO;
import main.dto.ErrorsDTO;
import main.dto.PostDTO;
import main.dto.UserDTO;
import main.api.errs.DocumentNotFoundExc;
import main.api.errs.NoAuthoraizedExc;
import main.api.request.ModerationRequest;
import main.api.request.NewPostRequest;
import main.api.response.AddingNewResponse;
import main.api.response.ApiPostResponse;
import main.model.*;
import main.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;


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

    @Autowired
    private GlobalSettingsRepository settingsRepository;

    private AuthCheckService authCheckService;

    public ApiPostService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public ApiPostResponse getApiPostResponse(Pageable pageable, String mode) {
        List<Post> posts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        switch (mode) {
            case ("recent"):
                posts = postRepository.findAllOrderByPostTimeDesc(pageable, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
                break;
            case ("popular"):
                posts = postRepository.findAllByComment(pageable,  new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
                break;
            case ("best"):
                posts = postRepository.findAllByLikes(pageable,  new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
                break;
            case ("early"):
                posts = postRepository.findAllOrderByPostTime(pageable, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
                break;

        }

        ApiPostResponse apiPostResponse = makePostResponse(posts);
        return apiPostResponse;
    }

    public ApiPostResponse searchPostResponse(Pageable pageable, String theQuery) {
        List<Post> posts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        theQuery.trim();
        if (theQuery.isEmpty()) {
            theQuery = "recent";
        }

        switch (theQuery) {
            case ("recent"):
                posts = postRepository.findAllOrderByPostTimeDesc(pageable, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
                break;
            default:
                posts.addAll(postRepository.findByTitleContaining(pageable, theQuery, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent());
                posts.addAll(postRepository.findAllByTextContent(pageable, theQuery, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent());
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
            postDTO.setTimestamp(post.getPostTime().getTime());
            postDTO.setUser(user);
            postDTO.setTitle(post.getTitle());
            postDTO.setText(post.getTextContent());
            postDTO.setLikeCount(
                    votesRepository.findAllByPostAndValue(post, (byte) 1).size()
            );
            postDTO.setDislikeCount(
                    votesRepository.findAllByPostAndValue(post, (byte) -1).size()
            );
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

            if (principal != null) {
                if (authUser.getIs_moderator() != 1 && !authUser.equals(post.getUser())) {
                    post.setViewCount(post.getViewCount() + 1);
                    postDTO.setViewCount(post.getViewCount());
                    postRepository.save(post);
                } else {
                    postDTO.setViewCount(post.getViewCount());
                }
            }
        }
        return postDTO;
    }

    public AddingNewResponse moderatePost(Principal principal, ModerationRequest moderationRequest) {
        User authUser =  authCheckService.getAuthUser(principal);
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        if (authUser != null) {
            if (authUser.getIs_moderator() == 1) {
                Optional<Post> optionalPost = postRepository.findById(moderationRequest.getPostId());
                if (optionalPost.isPresent()) {
                    Post post = optionalPost.get();
                    switch (moderationRequest.getDecision()) {
                        case ("accept"):
                            post.setModerationStatus(ModerationStatus.ACCEPTED);
                            break;
                        case ("decline"):
                            post.setModerationStatus(ModerationStatus.DECLINED);
                            break;
                    }
                    post.setModerator(authUser);
                    postRepository.save(post);
                    addingNewResponse.setResult(true);
                }
            }
        }
        return addingNewResponse;
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
        List<Post> posts = postRepository.findPostByPostTimeContaining(pageable, timestamp, stopTimestamp, ModerationStatus.ACCEPTED).getContent();
        ApiPostResponse apiPostResponse = makePostResponse(posts);

        return apiPostResponse;
    }

    public ApiPostResponse getPostsByTag (Pageable pageable, String tag) {

        ApiPostResponse apiPostResponse = new ApiPostResponse();
        Calendar calendar = Calendar.getInstance();

        if (tag.isEmpty()) {
            return apiPostResponse;
        }
        else {
            List<Post> posts = postRepository.findAllByTag(pageable, tag, new Timestamp(calendar.getTimeInMillis()), ModerationStatus.ACCEPTED).getContent();
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
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        if (!principal.equals(null)) {
            User user = authCheckService.getAuthUser(principal);
//        if (!user.equals(null)) {
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

    public AddingNewResponse addLikeToPost(int post_id, Principal principal, byte value) throws NoAuthoraizedExc {
        AddingNewResponse addingNewResponse = new AddingNewResponse();
//        User user = authCheckService.getAuthUser(principal);
        Calendar calendar = Calendar.getInstance();
        if (!principal.equals(null)) {
            User user = authCheckService.getAuthUser(principal);
            Optional<Post> optionalPost = postRepository.findById(post_id);
            if (optionalPost.isPresent()) {
                Optional<Votes> optionalVotes = votesRepository.findByPostAndUser(optionalPost.get(), user);
                if (optionalVotes.isPresent()) {
                    Votes vote = optionalVotes.get();
                    if (vote.getValue() == value) {
                        addingNewResponse.setResult(false);
                    }
                    else {
                        vote.setValue(value);
                        vote.setVoteTime(new Timestamp(calendar.getTimeInMillis()));
                        votesRepository.save(vote);
                        addingNewResponse.setResult(true);
                    }
                }
                else {
                    Votes vote = new Votes();
                    vote.setUser(user);
                    vote.setPost(optionalPost.get());
                    vote.setValue(value);
                    vote.setVoteTime(new Timestamp(calendar.getTimeInMillis()));
                    votesRepository.save(vote);
                    addingNewResponse.setResult(true);
                }
            }

        }
        else {
            throw new NoAuthoraizedExc();
        }
        return addingNewResponse;
    }

    public AddingNewResponse rewritePost(int id, Principal principal, NewPostRequest newPostRequest) {
//        User user = authCheckService.getAuthUser(principal);
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        Calendar calendar = Calendar.getInstance();
        int textLength = 50;
        int titleLength = 3;
        if (!authCheckService.getAuthUser(principal).equals(null)) {
            User user = authCheckService.getAuthUser(principal);
            Post post = postRepository.findById(id).get();
            ErrorsDTO errorsDTO = new ErrorsDTO();
            if (newPostRequest.getTitle().isEmpty() || newPostRequest.getTitle().length() < titleLength) {
                System.out.println("e1");
                errorsDTO.setTitleError();
                addingNewResponse.setResult(false);
            }
            if (newPostRequest.getText().length() < textLength) {
                System.out.println("e2");
                errorsDTO.setTextError();
                addingNewResponse.setResult(false);
            }
            if (addingNewResponse.isResult()) {
                addingNewResponse.setResult(true);
                post.setTitle(newPostRequest.getTitle());
                post.setTextContent(newPostRequest.getText());
                if (newPostRequest.getTimestamp().before(calendar.getTime())) {
                    newPostRequest.setTimestamp(new Timestamp(calendar.getTimeInMillis()));
                }
                post.setPostTime(newPostRequest.getTimestamp());
                post.setStatus(newPostRequest.getActive());
                List<Tag2Post> tag2Posts = tag2PostRepository.findByPost(post);
                List<String> tags = new ArrayList<>();
                tag2Posts.forEach(tag2Post -> {
                    tags.add(tag2Post.getTag().getName());
                });
                if (!newPostRequest.getTags().isEmpty()) {
                    newPostRequest.getTags().forEach(s -> {
                        if (!tags.contains(s)) {
                            Optional<Tag> optionalTag = tagsRepository.findByName(s);
                            Tag tag;
                            if (!optionalTag.isPresent()) {
                                tag = new Tag();
                                tag.setName(s);
                                tagsRepository.save(tag);
                            }
                            else {
                                tag = optionalTag.get();
                            }
                            Tag2Post tag2Post = new Tag2Post();
                            tag2Post.setPost(post);
                            tag2Post.setTag(tag);
                            tag2PostRepository.save(tag2Post);
                        }
                    });
                }
                if (user.getIs_moderator() != 1) {
                    post.setModerationStatus(ModerationStatus.NEW);
                }
                postRepository.save(post);
            }
            else {
                addingNewResponse.setError(errorsDTO);
            }
        }
        return addingNewResponse;
    }

    public AddingNewResponse addPost(Principal principal, Timestamp timestamp, byte active, String title, String text,
                                     List<String> tags) {

        AddingNewResponse addingNewResponse = new AddingNewResponse();
        Settings settings = settingsRepository.findByCode("POST_PREMODERATION");
        if (!authCheckService.getAuthUser(principal).equals(null)) {
            User user = authCheckService.getAuthUser(principal);
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
                if (settings.getValue().equals("NO")) {
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                }
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
            else {
                addingNewResponse.setError(errorsDTO);
            }
        }
        else {
            throw new NoAuthoraizedExc();
        }
        return addingNewResponse;
    }

    private ApiPostResponse makePostResponse (List<Post> posts) {
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        Calendar calendar = Calendar.getInstance();
//        posts = posts.stream().filter(x -> x.getStatus()==1
//                && x.getModerationStatus().equals(ModerationStatus.ACCEPTED)
//                && x.getPostTime().before((calendar.getTime())))
//                .collect(Collectors.toList());
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
