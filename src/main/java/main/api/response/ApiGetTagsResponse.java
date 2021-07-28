package main.api.response;

import main.dto.TagDTO;

import java.util.ArrayList;
import java.util.List;

public class ApiGetTagsResponse {

    List<TagDTO> tags;

    public ApiGetTagsResponse() {
        this.tags = new ArrayList<>();
    }

    public void addTag(TagDTO tagDTO) {
        tags.add(tagDTO);
    }

    public List<TagDTO> getTags() {
        return tags;
    }
}
