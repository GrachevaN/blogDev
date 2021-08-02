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
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public AddingNewResponse profileEdit(ProfileUpdateRequest profileUpdateRequest, Principal principal, HttpServletRequest request) throws IOException {

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
                imageRemove(user.getPhoto(), request);
            }
            AddingNewResponse photoResponse = addImageService.addImage(profileUpdateRequest.getPhoto(), request);
            if(photoResponse.isResult()) {
                user.setPhoto(photoResponse.getImageValue());
                imageResize(photoResponse.getImageValue(), request);
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


    public AddingNewResponse profileEditWithoutPhoto(NoPhotoProfileUpdate noPhotoProfileUpdate, Principal principal, HttpServletRequest request) throws IOException {

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
                imageRemove(uri, request);
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



    public void imageRemove(String uri, HttpServletRequest request) {

        String realPath = request.getServletContext().getRealPath(uri);
        File file = new File(realPath);
//        File file = new File(uri);
        file.delete();
        String folderPath = uri.substring(0, uri.lastIndexOf("\\"));
        Path directory = Paths.get(folderPath);
        if (Files.exists(directory)) {
            File fileDir = new File(folderPath);
            if (fileDir.listFiles().length == 0) {
                fileDir.delete();
            }
        }
    }

    public void imageResize(String uri, HttpServletRequest request) {
        int imageWidth = imageSize;
        int imageHeight = imageSize;
        String realPath = request.getServletContext().getRealPath(uri);
        File file = new File(realPath);
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

            String imageType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            File newFile = new File(realPath.substring(0, realPath.lastIndexOf("\\")) + "\\" + file.getName());
            file.delete();
            ImageIO.write(scaledImage, imageType, newFile);
            System.out.println("resized");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
