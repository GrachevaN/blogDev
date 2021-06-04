package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import main.serializer.UserSerializer;
import main.model.User;

public class AuthCheckResponse {

    private boolean result;


    @JsonSerialize(using = UserSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public AuthCheckResponse() {
        this.result = false;
    }


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
