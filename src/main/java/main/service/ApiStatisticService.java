package main.service;

import main.api.response.StatisticResponse;
import main.model.Post;
import main.model.Settings;
import main.model.User;
import main.repository.GlobalSettingsRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.repository.VotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ApiStatisticService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private GlobalSettingsRepository settingsRepository;

    @Autowired
    private VotesRepository votesRepository;

    private AuthCheckService authCheckService;

    public ApiStatisticService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public StatisticResponse getUserStatistic(Principal principal) {
        User authUser =  authCheckService.getAuthUser(principal);
        StatisticResponse statisticResponse = new StatisticResponse();
        if (!authUser.equals(null)) {
            List<Post> postList = postRepository.findAllByUserOrderByPostTime(authUser);
            statisticResponse.setPostsCount(postList.size());
            statisticResponse.setViewsCount(postList.stream().mapToInt(Post::getViewCount).sum());
            statisticResponse.setFirstPublication(postList.get(0).getPostTime().getTime());
            statisticResponse.setDislikesCount(postList.stream().mapToInt(post -> votesRepository.findAllByPostAndValue(post, (byte) -1).size()).sum());
            statisticResponse.setLikesCount(postList.stream().mapToInt(post -> votesRepository.findAllByPostAndValue(post, (byte) 1).size()).sum());
        }
        return statisticResponse;
    }

    public StatisticResponse getAllUserStatistic(Principal principal) {
        Settings settings = settingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        User authUser = authCheckService.getAuthUser(principal);
        StatisticResponse statisticResponse = new StatisticResponse();
        if (settings.getValue().equals("YES") || authUser.getIs_moderator() == 1) {
                List<Post> postList = postRepository.findAllPostByPostTime();
                statisticResponse.setPostsCount(postList.size());
                statisticResponse.setViewsCount(postList.stream().mapToInt(Post::getViewCount).sum());
                statisticResponse.setFirstPublication(postList.get(0).getPostTime().getTime());
                statisticResponse.setDislikesCount(postList.stream().mapToInt(post -> votesRepository.findAllByValue((byte) -1).size()).sum());
                statisticResponse.setLikesCount(postList.stream().mapToInt(post -> votesRepository.findAllByValue((byte) 1).size()).sum());
        }
        return statisticResponse;
    }


}
