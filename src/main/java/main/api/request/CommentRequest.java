package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequest {


    @JsonProperty("parent_id")
    private int parentId;
    @JsonProperty("post_id")
    private int postId;
    private String text;

}
