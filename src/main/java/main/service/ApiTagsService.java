package main.service;


import main.DTO.TagDTO;
import main.api.response.ApiGetTagsResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ApiTagsService {

    @Autowired
    private TagsRepository tagsRepository;


    @Autowired
    private PostRepository postRepository;

    public ApiGetTagsResponse getTags (String tagName) {
        ApiGetTagsResponse apiGetTagsResponse = new ApiGetTagsResponse();
        List<Tag> tags = tagsRepository.findTagsByPostsCount();
        Calendar calendar = Calendar.getInstance();
        List<Post> posts = postRepository.findAll();

        List<Tag> allTags = tagsRepository.findAll();
        posts = posts.stream().filter(x -> x.getStatus()==1
                && x.getModerationStatus().equals(ModerationStatus.NEW)
                && x.getPostTime().before((calendar.getTime())))
                .collect(Collectors.toList());
        long postCount = posts.size();
        List<Tag> searchedTags;
        if (!tagName.equals("")) {
            searchedTags = tagsRepository.findTagsByNameContaining(tagName);
        }
        else{
            searchedTags = tags;
        }
        List<Tag> finalSearchedTags = searchedTags;
        double normalizationCoefficient = findNormalizationCoefficient(tags, allTags, postCount);

        allTags.forEach(tag -> {
            double currentPostCount = finalSearchedTags.stream().filter(tag1 -> tag1.getName().equals(tag.getName())).count();
            if (currentPostCount > 0) {
                TagDTO tagDTO = new TagDTO();
                tagDTO.setName(tag.getName());
                tagDTO.setWeight( currentPostCount / postCount * normalizationCoefficient);
                apiGetTagsResponse.addTag(tagDTO);
            }
        });

        return apiGetTagsResponse;
    }

    private double findNormalizationCoefficient (List<Tag> finalTags, List<Tag> allTags, long postCount) {
        AtomicLong bestTagPostCount = new AtomicLong();
        allTags.forEach(tag -> {
            long x = finalTags.stream().filter(tag1 ->
                    tag1.getName().equals(tag.getName())
            ).count();
            if (x > bestTagPostCount.get()) {
                bestTagPostCount.set(x);
            }
        });
        double normalizationCoefficient = postCount / bestTagPostCount.get();
        return normalizationCoefficient;
    }


}
