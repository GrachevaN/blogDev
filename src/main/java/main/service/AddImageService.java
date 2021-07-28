package main.service;


import main.api.response.AddingNewResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AddImageService {

    static int foldLenght = 3;
    private int imgSize = 36;

    private final AuthCheckService authCheckService;

    public AddImageService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public AddingNewResponse addImage(MultipartFile newImage) throws IOException {

        AddingNewResponse addingNewResponse = new AddingNewResponse();

        String imageType = newImage.getOriginalFilename().substring(newImage.getOriginalFilename().lastIndexOf(".") + 1);
        if (imageType.equals("png") || imageType.equals("jpg")) {
            StringBuilder dir = new StringBuilder();
//            dir.append("src/main/resources/upload");
            dir.append("upload");
            for (int i = 0; i<3; i++) {
                dir.append("/");
                dir.append(authCheckService.generateCode(foldLenght));
            }
            Path theDir = Paths.get(dir.toString());
            Files.createDirectories(theDir);

            try (InputStream in = newImage.getInputStream();
                 OutputStream out = new FileOutputStream(theDir.toString() + "/" + newImage.getOriginalFilename()))
            {
                FileCopyUtils.copy(in, out);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }

            addingNewResponse.setResult(true);
            addingNewResponse.setImageValue(theDir.toString() + "/" + newImage.getOriginalFilename());
        }
        else {
            addingNewResponse.setResult(false);
        }
        System.out.println(addingNewResponse.isResult());
        return addingNewResponse;


    }



}
