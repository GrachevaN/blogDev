package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.dto.UserDTO;

@Data
//@JsonIgnore()
public class LoginResponse {

    private boolean result;

    @JsonProperty("user")
    private UserDTO user;

    public UserDTO getUser() {
        return user;
    }

//    private UserLoginResponse;

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
