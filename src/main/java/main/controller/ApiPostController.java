package main.controller;


import main.api.response.ApiPostResponse;
import main.service.ApiPostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private ApiPostService apiPostService;

    public ApiPostController(ApiPostService apiPostService) {
        this.apiPostService = apiPostService;
    }


    @GetMapping("/")
    public ApiPostResponse getApiPosts (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String mode
    ) {
        Pageable pageable = PageRequest.of(offset, limit);
//        Pageable pageable = new OffsetBasedPageRequest(limit, offset);
        HttpStatus.resolve(200);
        return apiPostService.getApiPostResponse(pageable, mode);

    }

}
