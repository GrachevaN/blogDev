package main.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import main.model.User;

import java.io.IOException;

public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        boolean moderation = (user.getIs_moderator() == 1) ? true : false;
        jsonGenerator.writeStartObject("user");
        jsonGenerator.writeNumberField("id", user.getId());
        jsonGenerator.writeStringField("name", user.getName());
        jsonGenerator.writeStringField("photo", user.getPhoto());
        jsonGenerator.writeStringField("email", user.getEmail());
        jsonGenerator.writeBooleanField("moderation", moderation);
        jsonGenerator.writeNumberField("moderationCount", user.getModerationCount());
        jsonGenerator.writeEndObject();
    }
}
