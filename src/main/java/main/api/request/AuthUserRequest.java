package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
public class AuthUserRequest {

    @JsonProperty("e_mail")
    private String email;
    String password;
    String name;
    String captcha;
    String captcha_secret;
    String repeatPassword;

}
