package main.api.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String name;
    private String email;
    private String password;
    private int removePhoto;
    private MultipartFile photo;

}
