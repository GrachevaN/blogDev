package main.service;

import main.DTO.PostDTO;
import main.DTO.UserDTO;
import main.api.response.ApiPostResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.repository.CommentsRepository;
import main.repository.PostRepository;
import main.repository.VotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ApiPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    public ApiPostResponse getApiPostResponse(Pageable pageable, String mode) {
        ApiPostResponse apiPostResponse = new ApiPostResponse();
        Calendar calendar = Calendar.getInstance();
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

        posts = posts.stream().filter(x -> x.getStatus()==1
                && x.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                && x.getPostTime().before((calendar.getTime())))
                .collect(Collectors.toList());

        posts.forEach(
                post -> {
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
                    postDTO.setLikeCount(
                            votesRepository.findAll().stream().filter(vote ->
                                    vote.getPost().equals(post)
                                            && vote.getValue() == -1
                            ).count()
                    );
                    postDTO.setAnnounce(announceCreate(post.getTextContent()));
                    apiPostResponse.increaseCount();
                    apiPostResponse.addPost(postDTO);
                }
        );
//        Collections.sort(apiPostResponse.getPosts());


        return apiPostResponse;
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
