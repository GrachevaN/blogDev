package main.api.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiCalendarResponse {

    List<Integer> years;


    Map<String, Integer> posts;


    public ApiCalendarResponse() {
        this.years = new ArrayList<>();
        this.posts = new HashMap<>();
    }

    public void addPost (String date) {
        if (!posts.keySet().contains(date)) {
            posts.put(date, 0);
        }
        posts.put(date, posts.get(date) + 1);
    }

    public Map<String, Integer> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Integer> posts) {
        this.posts = posts;
    }

    public void addYear(Integer year) {
        if (!years.contains(year)) {
            years.add(year);
        }
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
}
