package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserDTO {

    @JsonProperty("id")
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String photo;
    @JsonProperty("email")
    private String email;

    @JsonProperty(value = "moderation", defaultValue = "nontrue")
    private boolean moderation;

    @JsonProperty(value = "settings", defaultValue = "false")
    private boolean settings;

    @JsonProperty(value = "moderationCount", defaultValue = "-200")
    private long moderationCount;

    public UserDTO(int id, String name, String photo, String email, byte Is_moderator) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.email = email;
        this.moderation =  (Is_moderator == 1) ? true : false;
    }

    public UserDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setModeratorStatus() {
        this.settings = true;
        this.moderation = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isModeration() {
        return moderation;
    }

    public void setModeration(boolean moderation) {
        this.moderation = moderation;
    }

    public long getModerationCount() {
        return moderationCount;
    }

    public void setModerationCount(long moderationCount) {
        this.moderationCount = moderationCount;
    }


}
