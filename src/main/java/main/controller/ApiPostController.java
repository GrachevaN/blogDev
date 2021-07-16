package main.controller;


import main.DTO.PostDTO;
import main.api.request.NewPostRequest;
import main.api.response.AddingNewResponse;
import main.api.response.ApiPostResponse;
import main.service.ApiPostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final ApiPostService apiPostService;

    public ApiPostController(ApiPostService apiPostService) {
        this.apiPostService = apiPostService;
    }


    @GetMapping
//    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> getApiPosts (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String mode
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.getApiPostResponse(pageable, mode));
    }

    @PostMapping
    public ResponseEntity<AddingNewResponse> addPost(
            @RequestBody NewPostRequest newPostRequest
            , Principal principal
            ) {
        return ResponseEntity.ok(apiPostService.addPost(principal, newPostRequest.getTimestamp(), newPostRequest.getActive(),
                newPostRequest.getTitle(), newPostRequest.getText(), newPostRequest.getTags()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ApiPostResponse> getSearchedPosts(
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String query
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.searchPostResponse(pageable, query));
    }

    @GetMapping("/byDate")
    public ResponseEntity<ApiPostResponse> getPostByDate (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam String date
    ){
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.getPostByDate(pageable, date));
    }

    @GetMapping("/byTag")
    public ResponseEntity<ApiPostResponse> getPostByTag (
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam (defaultValue = "") String tag
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.getPostsByTag(pageable, tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById (
            @PathVariable int id, Principal principal
    ) {
        return ResponseEntity.ok(apiPostService.getPostById(id, principal));
    }

    @GetMapping("/moderation")
    public ResponseEntity<ApiPostResponse> getPostForModeration(
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam(defaultValue = "recent") String status
            , Principal principal
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.getModeratorsPosts(pageable, status, principal));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiPostResponse> getMyPosts(
            @RequestParam(defaultValue = "0") int offset
            , @RequestParam(defaultValue = "10") int limit
            , @RequestParam String status
            , Principal principal
    ) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return ResponseEntity.ok(apiPostService.getMyPosts(pageable, status, principal));
    }


}
