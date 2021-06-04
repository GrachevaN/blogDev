package main.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import main.DTO.PostDTO;

import java.io.IOException;
import java.util.List;

public class PostSerializer extends JsonSerializer<List<PostDTO>> {

//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("");


    @Override
    public void serialize(List<PostDTO> postDTOS, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartArray();
        for (PostDTO postDTO : postDTOS) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", postDTO.getId());
            jsonGenerator.writeNumberField("timestamp", postDTO.getTimestamp().getTime());
//            jsonGenerator.writeObjectField("post", postDTO);
            jsonGenerator.writeObjectFieldStart("user");
            jsonGenerator.writeNumberField("id", postDTO.getUser().getId());
            jsonGenerator.writeStringField("name", postDTO.getUser().getName());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeStringField("title", postDTO.getTitle());
            jsonGenerator.writeStringField("announce", postDTO.getAnnounce());
            jsonGenerator.writeNumberField("likeCount", postDTO.getLikeCount());
            jsonGenerator.writeNumberField("dislikeCount", postDTO.getDislikeCount());
            jsonGenerator.writeNumberField("commentCount", postDTO.getCommentCount());
            jsonGenerator.writeNumberField("viewCount", postDTO.getViewCount());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

    }
}
