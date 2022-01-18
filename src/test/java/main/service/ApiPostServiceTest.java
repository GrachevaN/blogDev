package main.service;

import main.api.response.ApiPostResponse;
import main.dto.PostDTO;
import main.repository.CommentsRepository;
import main.repository.PostRepository;
import main.repository.VotesRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class ApiPostServiceTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiPostService apiPostService;

    private ApiPostResponse apiPostResponseTest;



    @BeforeEach
    void setUp() {
        apiPostResponseTest = new ApiPostResponse();
    }

    @AfterEach
    void tearDown() {
        apiPostResponseTest = null;
    }

    @Test
    public void contextLoads() {
    }



    @Test
    void getRecentPostResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        String mode = "recent";
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("hello, new post");
        postDTO.setId(33);

        ApiPostResponse apiPostResponse = apiPostService.getApiPostResponse(pageable, mode);
        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());

    }

    @Test
    void getPopularPostResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        String mode = "popular";
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("title2");
        postDTO.setId(1);

        ApiPostResponse apiPostResponse = apiPostService.getApiPostResponse(pageable, mode);
        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());

    }

    @Test
    void getBestPostResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        String mode = "best";
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("sadfghjk");
        postDTO.setId(5);

        ApiPostResponse apiPostResponse = apiPostService.getApiPostResponse(pageable, mode);
        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());

    }

    @Test
    void getEarlyPostResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        String mode = "early";
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("title3");
        postDTO.setId(19);

        ApiPostResponse apiPostResponse = apiPostService.getApiPostResponse(pageable, mode);
        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());

    }

    @Test
    void checkGettingLimitPagePostResponse() {
        int postPerPage = 10;
        Pageable pageable = PageRequest.of(0, postPerPage);
        String mode = "recent";

        ApiPostResponse apiPostResponse = apiPostService.getApiPostResponse(pageable, mode);
        assertEquals(apiPostResponse.getPosts().size(), postPerPage);

    }

    @Test
    void testSearchResponse() {
        String theQuery = "settings";
        Pageable pageable = PageRequest.of(0, 10);
        int postOkCount = 2;

        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("title4");
        postDTO.setId(20);

        ApiPostResponse apiPostResponse = apiPostService.searchPostResponse(pageable, theQuery);
        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());
        assertEquals(apiPostResponse.getPosts().size(), postOkCount);
    }

    @Test
    void getPostByTagTest() {
        String tag = "KB";
        Pageable pageable = PageRequest.of(0, 10);
        int postOkCount = 2;

        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("title2");
        postDTO.setId(1);

        ApiPostResponse apiPostResponse = apiPostService.getPostsByTag(pageable, tag);

        assertTrue(CoreMatchers.is(apiPostResponse.getPosts().get(0).getTitle()).matches(postDTO.getTitle()));
        assertEquals(apiPostResponse.getPosts().get(0).getId(), postDTO.getId());
        assertEquals(apiPostResponse.getPosts().size(), postOkCount);


    }
}