package main.controller;


import main.DTO.PostDTO;
import main.api.response.ApiPostResponse;
import main.service.ApiPostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private ApiPostService apiPostService;

    public ApiPostController(ApiPostService apiPostService) {
        this.apiPostService = apiPostService;
    }


    @GetMapping("/")
//    public ApiPostResponse getApiPosts (
    public ResponseEntity<?> getApiPosts (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String mode
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        HttpStatus.resolve(200);
        return apiPostService.getApiPostResponse(pageable, mode);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSearchedPosts(
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String theQuery
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
//        HttpStatus.resolve(200);
        return apiPostService.searchPostResponse(pageable, theQuery);
    }

    @GetMapping("/byDate")
    public ResponseEntity<?> getPostByDate (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam String theDate
    ){
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return apiPostService.getPostByDate(pageable, theDate);
    }

    @GetMapping("/byTag")
    public ResponseEntity<?> getPostByTag (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam (defaultValue = "") String tag
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return apiPostService.getPostsByTag(pageable, tag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById (
            @PathVariable int id, HttpSession session
    ) {
        return apiPostService.getPostById(id, session);
    }


}
