package main.controller;


import main.api.request.AuthUserRequest;
import main.api.request.LoginRequest;
import main.api.response.AddingNewResponse;
import main.api.response.AuthCaptchaResponse;
import main.api.response.LoginResponse;
import main.service.AuthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthCheckService authCheckService;

    @Autowired
    public ApiAuthController(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> authCheck (Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
//        String sessionId = session.getId();
        return ResponseEntity.ok(authCheckService.getAuthCheckResponse(principal));

    }

    @GetMapping("/captcha")
    public AuthCaptchaResponse getCaptcha () throws IOException {
        return authCheckService.getCaptcha();
    }

    @PostMapping("/register")
    public ResponseEntity<AddingNewResponse> registerNewUser(
//            @RequestBody AuthUserRequest authUserRequest
            @RequestParam String e_mail
            , @RequestParam String password
            , @RequestParam String name
            , @RequestParam String captcha
            , @RequestParam String captcha_secret
            , @RequestParam String repeatPassword
    ) {
//        if (!authUserRequest.getPassword().equals(authUserRequest.getRepeatPassword())) {
//            return ResponseEntity.ok(new AddingNewResponse());
//        }
//        return ResponseEntity.ok(authCheckService.registerNewUser(authUserRequest.getEmail(), authUserRequest.getCaptcha(),
//                authUserRequest.getCaptcha_secret(), authUserRequest.getPassword(), authUserRequest.getName()));
        if (!password.equals(repeatPassword)) {
            return ResponseEntity.ok(new AddingNewResponse());
        }
        return ResponseEntity.ok(authCheckService.registerNewUser(e_mail, captcha, captcha_secret, password, name));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse loginResponse = authCheckService.logUser(loginRequest);
//        return ResponseEntity.ok(authCheckService.logUser(loginRequest));
        return ResponseEntity.ok(loginResponse);
    }


    @GetMapping("/logout")
    public ResponseEntity<AddingNewResponse> userLogOut(
            HttpServletRequest request
    ) {

        return ResponseEntity.ok(authCheckService.logUserOut(request));
    }




}
