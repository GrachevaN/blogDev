package main.service;


import com.github.cage.Cage;
import com.github.cage.GCage;
import main.DTO.UserDTO;
import main.api.response.AddingNewUserResponse;
import main.api.response.AuthCaptchaResponse;
import main.api.response.AuthCheckResponse;
import main.model.CaptchaCodes;
import main.model.ModerationStatus;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AuthCheckService {


    @Autowired
    private static UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CaptchaRepository captchaRepository;

    public static Map<HttpSession, Integer> sessions = new HashMap();
    public static int captchaWidth = 100;
    public static int captchaHeight = 35;


    public AuthCheckResponse getAuthCheckResponse(HttpSession session) {
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();
        if (sessions.keySet().contains(session)) {
            authCheckResponse.setResult(true);
            Optional<User> optionalUser = userRepository.findById(sessions.get(session));

//            Optional<User> optionalUser = userRepository.findById(1);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                long moderationPostCount = postRepository.findAll().stream().filter(x -> x.getModerationStatus() == ModerationStatus.NEW).count();
                long moderationCount = (user.getIs_moderator() == 1) ? moderationPostCount : 0;
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getPhoto(), user.getEmail(), user.getIs_moderator());

                userDTO.setModerationCount(moderationCount);
                authCheckResponse.setUser(userDTO);
            }
        }
        return authCheckResponse;
    }

    public static Map<HttpSession, Integer> getSessions() {
        return sessions;
    }

    public static User getAuthUser(HttpSession session) {
        if (sessions.keySet().contains(session)) {
            Optional<User> optionalUser = userRepository.findById(sessions.get(session));

//            Optional<User> optionalUser = userRepository.findById(1);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                return user;
            }
        }
        return null;
    }

    public AuthCaptchaResponse getCaptcha() throws IOException {
        AuthCaptchaResponse authCaptchaResponse = new AuthCaptchaResponse();
        Cage cage = new GCage();


        OutputStream os = null;
//        try {
//            os = new FileOutputStream("src/main/resources/captcha.jpg", false);
        byte[] fileContent = cage.draw(cage.getTokenGenerator().next());
        ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);
        BufferedImage image = ImageIO.read(bais);
        image = Scalr.resize(image, captchaWidth, captchaHeight);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos );
        byte[] imageInByte=baos.toByteArray();

        String encodedString = "data:image/png;base64, " + Base64.getEncoder()
                .encodeToString(imageInByte);
        authCaptchaResponse.setImage(encodedString);
        UUID uuid1 = UUID.randomUUID();
        authCaptchaResponse.setSecret(String.valueOf(uuid1));

        addNewCaptcha(String.valueOf(uuid1), encodedString);
        checkOldCaptcha();

        return authCaptchaResponse;

    }

    public AddingNewUserResponse registerNewUser (String email, String captcha,
                                                  String captchaSecret, String password, String name) {
        AddingNewUserResponse addingNewUserResponse = new AddingNewUserResponse();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            addingNewUserResponse.setEmailError();
            addingNewUserResponse.setResult(false);
        }
        if (password.length() < 6) {
            addingNewUserResponse.setPasswordError();
            addingNewUserResponse.setResult(false);
        }
        CaptchaCodes captchaCodes = captchaRepository.findBySecretCode(captchaSecret);
        if (!captchaCodes.getCode().equals(captcha)) {
            addingNewUserResponse.setCaptchaError();
            addingNewUserResponse.setResult(false);
        }
        if (addingNewUserResponse.isResult()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            userRepository.save(user);
        }

        return addingNewUserResponse;
    }

    private void checkCaptcha () {
//        captchaRepository
    }

    private void addNewCaptcha(String secret, String encodedString) {
        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setCode(secret);
        captchaCodes.setSecretCode("encodedString");
        Date date = new Date();

        captchaCodes.setTime(new Timestamp(date.getTime()));
        captchaRepository.save(captchaCodes);
    }

    private void checkOldCaptcha () {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, calendar.get(calendar.HOUR) -1);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        List<CaptchaCodes> captchaCodes = captchaRepository.findCaptchaCodesByTimeBefore(timestamp);
        captchaCodes.forEach(captchaCodes1 -> {
            captchaRepository.delete(captchaCodes1);
        });
//        captchaRepository
    }

}
