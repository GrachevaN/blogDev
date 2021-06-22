package main.service;


import com.github.cage.Cage;
import com.github.cage.GCage;
import main.DTO.ErrorsDTO;
import main.DTO.UserDTO;
import main.api.request.LoginRequest;
import main.api.response.AddingNewResponse;
import main.api.response.AuthCaptchaResponse;
import main.api.response.LoginResponse;
import main.model.CaptchaCodes;
import main.model.ModerationStatus;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AuthCheckService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public static Map<String, Integer> sessions = new HashMap();
    public static int captchaWidth = 100;
    public static int captchaHeight = 35;


    public AuthCheckService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public LoginResponse getAuthCheckResponse(Principal principal) {
        LoginResponse loginResponse = getLoginResponse(principal.getName());
        return loginResponse;
    }

    public static Map<String, Integer> getSessions() {
        return sessions;
    }

    public User getAuthUser(Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
            return user;

        }
        return null;
    }


    private void deleteUserSession(HttpSession session) {
        sessions.remove(session.getId());
    }

    public AuthCaptchaResponse getCaptcha() throws IOException {
        AuthCaptchaResponse authCaptchaResponse = new AuthCaptchaResponse();
        Cage cage = new GCage();

        String captcha = generateCode();
        byte[] fileContent = cage.draw(captcha);
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

        addNewCaptcha(String.valueOf(uuid1), captcha);
        checkOldCaptcha();

        return authCaptchaResponse;

    }

    public LoginResponse logUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.
                authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(), loginRequest.getPassword()
                        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        LoginResponse loginResponse = getLoginResponse(user.getUsername());
        return loginResponse;
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);

        UserDTO userDTO = new UserDTO (
                currentUser.getId(), currentUser.getName(),
                currentUser.getPhoto(), currentUser.getEmail(),
                currentUser.getIs_moderator()
        );

        if (userDTO.isModeration()) {
            userDTO.setModeratorStatus();
            userDTO.setModerationCount(postRepository.findByModerationStatus(ModerationStatus.NEW).size());
        }
        else {
            userDTO.setModerationCount(0);
            userDTO.setModeration(false);
        }
        loginResponse.setUser(userDTO);
        return loginResponse;
    }


    public AddingNewResponse logUserOut (HttpServletRequest request) {
        HttpSession session = request.getSession();
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
        for (Cookie cookie: request.getCookies()) {
            cookie.setMaxAge(0);
        }
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        return addingNewResponse;
    }

    public AddingNewResponse registerNewUser (String email, String captcha,
                                              String captchaSecret, String password,
                                              String name) {

        Calendar calendar = Calendar.getInstance();
        AddingNewResponse addingNewResponse = new AddingNewResponse();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        ErrorsDTO errorsDTO = new ErrorsDTO();

        if (optionalUser.isPresent()) {
            errorsDTO.setEmailError();
            addingNewResponse.setResult(false);
        }

        if (password.length() < 6) {
            errorsDTO.setPasswordError();
            addingNewResponse.setResult(false);
        }

        CaptchaCodes captchaCodes = captchaRepository.findBySecretCode(captchaSecret);
        if (!captchaCodes.getCode().equals(captcha)) {
            errorsDTO.setCaptchaError();
            addingNewResponse.setResult(false);
        }

        if (addingNewResponse.isResult()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setIs_moderator((byte) 0);
            user.setRegistrationTime(new Timestamp(calendar.getTime().getTime()));
            userRepository.save(user);
        }
        else {
            addingNewResponse.setError(errorsDTO);
        }
        return addingNewResponse;
    }


    private String generateCode () {
        int length = 6;
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for( int i = 0; i < length; i++ ) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }


    private void addNewCaptcha(String secret, String encodedString) {

        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setCode(encodedString);
        captchaCodes.setSecretCode(secret);
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
