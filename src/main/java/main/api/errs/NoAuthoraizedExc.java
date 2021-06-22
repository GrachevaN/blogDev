package main.api.errs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorised User")
public class NoAuthoraizedExc extends RuntimeException {
}
