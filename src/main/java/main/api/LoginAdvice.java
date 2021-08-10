package main.api;


import main.api.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LoginAdvice {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginResponse> handleException(BadCredentialsException e) {
        LoginResponse loginResponse = new LoginResponse();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<LoginResponse> handleException(UsernameNotFoundException e) {
        LoginResponse loginResponse = new LoginResponse();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

}
