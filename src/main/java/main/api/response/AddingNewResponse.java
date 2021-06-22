package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import main.DTO.ErrorsDTO;

public class AddingNewResponse {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsDTO error;

    public ErrorsDTO getError() {
        return error;
    }

    public void setError(ErrorsDTO error) {
        this.error = error;
    }

    public AddingNewResponse() {
        this.result = true;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }


}
