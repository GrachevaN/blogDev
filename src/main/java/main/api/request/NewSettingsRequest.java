package main.api.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewSettingsRequest {

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticIsPublic;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiuserMode;

}
