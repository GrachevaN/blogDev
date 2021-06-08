package main.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import main.model.User;

import java.sql.Timestamp;

@JsonSerialize
public class PostDTO {

    @JsonProperty("id")
    private int id;

    @JsonProperty("timestamp")
    private long timestamp;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO user;

    @JsonProperty("title")
    private String title;

    @JsonProperty("announce")
    private String announce;
    @JsonProperty("likeCount")
    private long likeCount;
    @JsonProperty("dislikeCount")
    private long dislikeCount;
    @JsonProperty("viewCount")
    private int viewCount;
    @JsonProperty("commentCount")
    private long commentCount;


    public PostDTO(int id, Timestamp timestamp, UserDTO user, String title)  {
        this.id = id;
        this.timestamp = timestamp.getTime();
        this.user = user;
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

}
