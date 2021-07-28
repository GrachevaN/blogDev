package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordRestoreRequest {

    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    String captchaSecret;

}
