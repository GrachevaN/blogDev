package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoPhotoProfileUpdate {

    private String name;
    private String email;
    private String password;
    private int removePhoto;
    private String photo;

}
