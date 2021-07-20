package main.api.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
public class NewPostRequest {

    private Timestamp timestamp;
    byte active;
    String title;
    List<String> tags;
    String text;

    public NewPostRequest() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
