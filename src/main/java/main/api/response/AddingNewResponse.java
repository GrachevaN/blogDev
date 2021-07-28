package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import main.dto.ErrorsDTO;

@Getter
@Setter
public class AddingNewResponse {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorsDTO error;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(value = "id", defaultValue = "-200")
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageValue;

}
