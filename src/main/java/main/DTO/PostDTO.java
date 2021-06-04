package main.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import main.model.User;

import java.sql.Timestamp;

public class PostDTO implements Comparable<PostDTO>{

    private int id;
    private Timestamp timestamp;
    @JsonIgnore
    private User user;
    private String title;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private long commentCount;
    private String mode;


    public PostDTO(int id, Timestamp timestamp, User user, String title, String mode)  {
        this.id = id;
        this.timestamp = timestamp;
        this.user = user;
        this.title = title;
        this.mode = mode;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    @Override
    public int compareTo(PostDTO postDTO) {

        switch (mode) {
            case ("recent"):
                return this.getTimestamp().compareTo(this.getTimestamp());
            case ("popular"):
                return (int) (postDTO.getCommentCount() - this.getCommentCount());
            case ("best"):
                return (int) (postDTO.getLikeCount() - this.getLikeCount());
            case ("early"):
                return postDTO.getTimestamp().compareTo(this.getTimestamp());
        }


        return 0;
    }
}
