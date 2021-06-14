package main.controller;


import main.api.response.AddingNewUserResponse;
import main.api.response.AuthCaptchaResponse;
import main.api.response.AuthCheckResponse;
import main.service.AuthCheckService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthCheckService authCheckService;

    public ApiAuthController(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    @GetMapping("/check")
    public AuthCheckResponse authCheck (HttpSession session) {
//        String sessionId = session.getId();
        return authCheckService.getAuthCheckResponse(session);

    }

    @GetMapping("/captcha")
    public AuthCaptchaResponse getCaptcha () throws IOException {
        return authCheckService.getCaptcha();
    }

    @PostMapping("/register")
    public AddingNewUserResponse registerNewUser(
            @RequestParam String e_mail
            , @RequestParam String password
            , @RequestParam String name
            , @RequestParam String captcha
            , @RequestParam String captcha_secret
    ) {
        return authCheckService.registerNewUser(e_mail, captcha, captcha_secret, password, name);


    }




}
