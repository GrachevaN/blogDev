package main.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table (name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "is_active")
    private byte status;

    @Enumerated (EnumType.STRING)
//    @Column(columnDefinition = "enum")
    @NotNull
    private ModerationStatus moderationStatus = ModerationStatus.NEW;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User moderator;

    @NotNull
    @Column(name = "post_time")
    private Timestamp postTime;

    @NotNull
    private String title;

    @NotNull
    private String textContent;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

        public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public User getModeratorId() {
        return moderator;
    }

    public void setModeratorId(User moderator) {
        this.moderator = moderator;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}