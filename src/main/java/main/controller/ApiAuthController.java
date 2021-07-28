package main.controller;


import main.api.request.AuthUserRequest;
import main.api.request.LoginRequest;
import main.api.request.ProfileUpdateRequest;
import main.api.response.AddingNewResponse;
import main.api.response.AuthCaptchaResponse;
import main.api.response.LoginResponse;
import main.service.AuthCheckService;
import main.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthCheckService authCheckService;
    private final SettingsService settingsService;

    @Autowired
    public ApiAuthController(AuthCheckService authCheckService, SettingsService settingsService) {
        this.authCheckService = authCheckService;
        this.settingsService = settingsService;
    }

    @GetMapping("/check")
    public ResponseEntity<?> authCheck (Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(authCheckService.getAuthCheckResponse(principal));

    }

    @GetMapping("/captcha")
    public AuthCaptchaResponse getCaptcha () throws IOException {
        return authCheckService.getCaptcha();
    }

    @PostMapping("/restore")
    public ResponseEntity<?> restorePassword(
            @RequestBody ProfileUpdateRequest profileUpdateRequest
    ) {
        return ResponseEntity.ok(authCheckService.passwordRestore(profileUpdateRequest.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<AddingNewResponse> registerNewUser(
            @RequestBody AuthUserRequest authUserRequest
    ) {
        String status = settingsService.getMultiUserModeServiceStatus();
        if (status.equals("YES")) {
            return ResponseEntity.ok(authCheckService.registerNewUser(authUserRequest.getEmail(), authUserRequest.getCaptcha(),
                    authUserRequest.getCaptchaSecret(), authUserRequest.getPassword(), authUserRequest.getName()));
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse loginResponse = authCheckService.logUser(loginRequest);
//        return ResponseEntity.ok(authCheckService.logUser(loginRequest));
        return ResponseEntity.ok(loginResponse);
    }


    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AddingNewResponse> userLogOut(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(authCheckService.logUserOut(request));
    }


    @PostMapping("/password")
    public ResponseEntity<?> passwordRestore(
            @RequestBody AuthUserRequest authUserRequest) {

        return ResponseEntity.ok(authCheckService.newPasswordRestore(authUserRequest));
    }




}
