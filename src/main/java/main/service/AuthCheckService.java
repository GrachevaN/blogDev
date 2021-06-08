package main.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import main.DTO.UserDTO;
import main.api.response.AuthCheckResponse;
import main.model.ModerationStatus;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class AuthCheckService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    Map<HttpSession, Integer> sessions = new HashMap();


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


}
