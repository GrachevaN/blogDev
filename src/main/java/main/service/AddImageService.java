package main.service;


import main.api.response.AddingNewResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AddImageService {

    static int foldLenght = 3;
    private int imgSize = 36;

//    @Value("${upload.path}")
//    private String valUploadPath;

    private final AuthCheckService authCheckService;

    public AddImageService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public AddingNewResponse addImage(MultipartFile newImage, HttpServletRequest request) throws IOException {

        AddingNewResponse addingNewResponse = new AddingNewResponse();

        String imageType = newImage.getOriginalFilename().substring(newImage.getOriginalFilename().lastIndexOf(".") + 1);
        if (imageType.equals("png") || imageType.equals("jpg")) {
            StringBuilder dir = new StringBuilder();
            dir.append("/upload");
//            dir.append(valUploadPath);
            for (int i = 0; i<3; i++) {
                dir.append("/");
                dir.append(authCheckService.generateCode(foldLenght));
            }
            Path theDir = Paths.get(dir.toString());
            String uploadPath = theDir.toString() + "\\";
            String realPath = request.getServletContext().getRealPath(uploadPath);
            Files.createDirectories(Paths.get(realPath));

//            Files.createDirectories(Paths.get(uploadPath));
            try (InputStream in = newImage.getInputStream();
            OutputStream out = new FileOutputStream(realPath + "\\" + newImage.getOriginalFilename()))
//                 OutputStream out = new FileOutputStream(uploadPath + "\\" + newImage.getOriginalFilename()))
            {
                FileCopyUtils.copy(in, out);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }

            addingNewResponse.setResult(true);
            addingNewResponse.setImageValue(theDir.toString() + "\\" + newImage.getOriginalFilename());
        }
        else {
            addingNewResponse.setResult(false);
        }
        System.out.println(addingNewResponse.isResult());
        return addingNewResponse;


    }



}
