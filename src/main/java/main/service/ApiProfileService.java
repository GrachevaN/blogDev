package main.service;

import main.api.request.NoPhotoProfileUpdate;
import main.api.request.ProfileUpdateRequest;
import main.api.response.AddingNewResponse;
import main.dto.ErrorsDTO;
import main.model.User;
import main.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Service
public class ApiProfileService {

    @Autowired
    private UserRepository userRepository;

    private AuthCheckService authCheckService;

    private AddImageService addImageService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private int passLength = 6;
    private int imageSize = 36;

    public ApiProfileService(AuthCheckService authCheckService, AddImageService addImageService) {
        this.authCheckService = authCheckService;
        this.addImageService = addImageService;
    }

    public AddingNewResponse profileEdit(ProfileUpdateRequest profileUpdateRequest, Principal principal) throws IOException {

        AddingNewResponse addingNewResponse = new AddingNewResponse();
        addingNewResponse.setResult(true);
        User user = authCheckService.getAuthUser(principal);
        ErrorsDTO errorsDTO = new ErrorsDTO();
        if (profileUpdateRequest.getPassword() != null) {
            if (profileUpdateRequest.getPassword().length() >= passLength) {
                user.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword()));
            }
            else {
                errorsDTO.setPasswordError();
                addingNewResponse.setResult(false);
            }
        }
        if (profileUpdateRequest.getRemovePhoto() == 0 && profileUpdateRequest.getPhoto() != null) {
            if (user.getPhoto() != null) {
                imageRemove(user.getPhoto());
            }
            AddingNewResponse photoResponse = addImageService.addImage(profileUpdateRequest.getPhoto());
            if(photoResponse.isResult()) {
                user.setPhoto(photoResponse.getImageValue());
                imageResize(photoResponse.getImageValue());
            }
            else {
                errorsDTO.setPhoto();
                addingNewResponse.setResult(false);
            }
        }
        if (profileUpdateRequest.getName() != null) {
            user.setName(profileUpdateRequest.getName());
        }
        if (profileUpdateRequest.getEmail() != null) {
            Optional<User> optionalUser = userRepository.findByEmail(profileUpdateRequest.getEmail());
            if (!optionalUser.isPresent()) {
                user.setEmail(profileUpdateRequest.getEmail());
            }
            else {
                errorsDTO.setEmailError();
                addingNewResponse.setResult(false);
            }
        }
        if (!addingNewResponse.isResult()) {
            addingNewResponse.setError(errorsDTO);
        }
        else {
            System.out.println("u are perfect");
            userRepository.save(user);
        }
        return addingNewResponse;
    }


    public AddingNewResponse profileEditWithoutPhoto(NoPhotoProfileUpdate noPhotoProfileUpdate, Principal principal) throws IOException {

        AddingNewResponse addingNewResponse = new AddingNewResponse();
        addingNewResponse.setResult(true);
        User user = authCheckService.getAuthUser(principal);
        ErrorsDTO errorsDTO = new ErrorsDTO();
        if (noPhotoProfileUpdate.getPassword() != null) {
            if (noPhotoProfileUpdate.getPassword().length() >= passLength) {
                user.setPassword(passwordEncoder.encode(noPhotoProfileUpdate.getPassword()));
            }
            else {
                errorsDTO.setPasswordError();
                addingNewResponse.setResult(false);
            }
        }
        if (noPhotoProfileUpdate.getRemovePhoto()==1 && noPhotoProfileUpdate.getPhoto() == null) {
            String uri = user.getPhoto();
            user.setPhoto(null);
//            delete photo
            if (uri != null) {
                imageRemove(uri);
            }
        }
        if (noPhotoProfileUpdate.getName() != null) {
            user.setName(noPhotoProfileUpdate.getName());
        }
        if (noPhotoProfileUpdate.getEmail() != null) {
            Optional<User> optionalUser = userRepository.findByEmail(noPhotoProfileUpdate.getEmail());
            if (!optionalUser.isPresent()) {
                user.setEmail(noPhotoProfileUpdate.getEmail());
            }
            else {
                errorsDTO.setEmailError();
                addingNewResponse.setResult(false);
            }
        }
        if (!addingNewResponse.isResult()) {
            addingNewResponse.setError(errorsDTO);
            System.out.println("error found");
        }
        else {
            System.out.println("u are perfect");
            userRepository.save(user);
        }
        return addingNewResponse;
    }



    public void imageRemove(String uri) {

        File file = new File(uri);

        if (file.delete()) {
            System.out.println(file.getName() + " deleted");
        } else {
            System.out.println(file.getName() + " not deleted");
        }
        String folderPath = uri.substring(0, uri.lastIndexOf("/"));
        File directory = new File(folderPath);
        if (directory != null) {

            if (directory.listFiles().length == 0) {
                directory.delete();
            }
        }

    }

    public void imageResize(String uri) {
        int imageWidth = imageSize;
        int imageHeight = imageSize;
        File file = new File(uri);
        try {
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                System.out.println("null image");
                return;
            }
            else if (image.getWidth() <= imageSize && image.getHeight() <= imageSize) {
                System.out.println("ok size");
                return;
            }

            if (image.getWidth() <= imageSize) {
                imageWidth = image.getWidth();
            }
            if (image.getHeight() <= imageSize) {
                imageHeight = image.getHeight();
            }

            BufferedImage scaledImage = Scalr.resize(image, imageWidth, imageHeight);
            file.delete();

            String imageType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            File newFile = new File(uri.substring(0, uri.lastIndexOf("/")) + "/" + file.getName());
            ImageIO.write(scaledImage, imageType, newFile);
            System.out.println("resized");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
