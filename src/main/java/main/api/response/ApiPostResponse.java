package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import main.DTO.PostDTO;
import main.serializer.PostSerializer;

import java.util.ArrayList;
import java.util.List;

public class ApiPostResponse {

    private int count;

    @JsonSerialize(using = PostSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PostDTO> posts;


    public ApiPostResponse() {
        this.posts = new ArrayList<>();
    }

    public void addPost(PostDTO post) {
        posts.add(post);
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount() {
        this.count++;
    }
}
