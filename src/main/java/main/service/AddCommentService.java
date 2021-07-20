package main.service;

import main.DTO.ErrorsDTO;
import main.api.request.CommentRequest;
import main.api.response.AddingNewResponse;
import main.model.Comment;
import main.model.Post;
import main.model.User;
import main.repository.CommentsRepository;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

@Service
public class AddCommentService {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PostRepository postRepository;

    private AuthCheckService authCheckService;

    public AddCommentService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    public AddingNewResponse addComment(Principal principal, CommentRequest commentRequest){
        User authUser =  authCheckService.getAuthUser(principal);
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        Calendar calendar = Calendar.getInstance();
        if (!authUser.equals(null)) {
            Optional<Post> optionalPost = postRepository.findById(commentRequest.getPostId());
//        if (new Integer(commentRequest.getParentId()) != null) {
            Optional<Comment> optionalComment = commentsRepository.findById(commentRequest.getParentId());
            if (commentRequest.getText().isEmpty() && commentRequest.getText().trim().length() > 5)
            {
                System.out.println("3");
             addingNewResponse.setResult(false);
            }
            else if (!optionalPost.isPresent()
                    && (!optionalComment.isPresent() && commentRequest.getParentId() != 0)
            ) {
                System.out.println("4");
                addingNewResponse.setResult(false);
                ErrorsDTO errorsDTO = new ErrorsDTO();
                errorsDTO.setTextCommentError();
                addingNewResponse.setError(errorsDTO);
            }
            else {
                System.out.println("2");
                Comment comment = new Comment();
                comment.setUser(authUser);
                comment.setCommentPostTime(new Timestamp(calendar.getTimeInMillis()));
                comment.setText(commentRequest.getText());
                comment.setPost(optionalPost.get());
                if (commentRequest.getParentId() != 0) {
                    comment.setParent(optionalComment.get());
                }
                System.out.println(commentRequest.getParentId());
                commentsRepository.save(comment);
                addingNewResponse.setId(comment.getId());
            }

        }
        return addingNewResponse;
    }

}
