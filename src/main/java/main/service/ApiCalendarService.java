package main.service;

import main.api.response.ApiCalendarResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiCalendarService {

    @Autowired
    private PostRepository postRepository;


    public ApiCalendarResponse getApiCalendar (int year) {
        ApiCalendarResponse apiCalendarResponse = new ApiCalendarResponse();
        Calendar calendar = Calendar.getInstance();

        if (year == 0 ) {
            year = calendar.get(calendar.YEAR);
        }

        List<Post> posts = postRepository.findAll();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        posts = posts.stream().filter(x -> x.getStatus()==1
                && x.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                && x.getPostTime().before((calendar.getTime())))
                .collect(Collectors.toList());

        posts.forEach(post -> {
            apiCalendarResponse.addYear(Integer.valueOf(sdfYear.format(post.getPostTime())));
        });
        int finalYear = year;
        posts.stream().filter(post -> Integer.valueOf(sdfYear.format(post.getPostTime())) == finalYear);
        posts.forEach(post -> {
            apiCalendarResponse.addPost(sdf.format(post.getPostTime()));
        });

        return apiCalendarResponse;
    }


}
