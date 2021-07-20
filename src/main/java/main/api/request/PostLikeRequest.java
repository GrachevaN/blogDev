package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostLikeRequest {

    @JsonProperty("post_id")
    private int postId;

    public PostLikeRequest() {
    }
}
