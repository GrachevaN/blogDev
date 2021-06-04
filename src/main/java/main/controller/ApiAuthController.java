package main.controller;


import main.api.response.AuthCheckResponse;
import main.service.AuthCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

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



}
