package main.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import main.DTO.TagDTO;

import java.io.IOException;
import java.util.List;

public class TagsSerializer extends JsonSerializer<List<TagDTO>> {

    @Override
    public void serialize(List<TagDTO> tagDTOS, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        for (TagDTO tagDTO: tagDTOS) {
            jsonGenerator.writeStartObject(tagDTO);
            jsonGenerator.writeEndObject();
        }
    }
}
