package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.DTO.ErrorsDTO;

public class AddingNewResponse {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsDTO error;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(value = "id", defaultValue = "-200")
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
