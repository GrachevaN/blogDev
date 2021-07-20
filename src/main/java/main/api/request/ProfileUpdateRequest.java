package main.api.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProfileUpdateRequest {

    private String name;
    private String email;
    private String password;
    private int removePhoto;

}
