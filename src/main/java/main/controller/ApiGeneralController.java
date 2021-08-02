package main.controller;


import main.api.request.*;
import main.api.response.*;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final ApiTagsService apiTagsService;
    private final ApiCalendarService apiCalendarService;
    private final ApiPostService apiPostService;
    private final AddCommentService addCommentService;
    private final ApiStatisticService apiStatisticService;
    private final AddImageService addImageService;
    private final ApiProfileService apiProfileService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, ApiTagsService apiTagsService, ApiCalendarService apiCalendarService, ApiPostService apiPostService, AddCommentService addCommentService, ApiStatisticService apiStatisticService, AddImageService addImageService, ApiProfileService apiProfileService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.apiTagsService = apiTagsService;
        this.apiCalendarService = apiCalendarService;
        this.apiPostService = apiPostService;
        this.addCommentService = addCommentService;
        this.apiStatisticService = apiStatisticService;
        this.addImageService = addImageService;
        this.apiProfileService = apiProfileService;
    }

    @GetMapping("/init")
    private InitResponse initResponse() {
        return initResponse;
    }


    @GetMapping("/settings")
    public ResponseEntity<?> settingsGet () {
            return ResponseEntity.ok(settingsService.getSettingsResponse());
    }


    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void putNewSettings(
            @RequestBody NewSettingsRequest newSettingsRequest
            , Principal principal
    ) {
        settingsService.putSettings(principal, newSettingsRequest);
    }

    @GetMapping("/tag")
    public ApiGetTagsResponse getTags (
            @RequestParam(defaultValue = "") String query
    ) {
        return apiTagsService.getTags(query);
    }

    @GetMapping("/calendar")
    public ApiCalendarResponse getPublicationCountPerYear(
            @RequestParam(defaultValue = "0") int year
    ) {
        return apiCalendarService.getApiCalendar(year);
    }

    @PostMapping(value = "/image", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postImage(
            @RequestParam("image") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        AddingNewResponse addingNewResponse = addImageService.addImage(file, request);
        if (addingNewResponse.isResult()) {
            return ResponseEntity.ok(addingNewResponse);
        }
        else {
            return new ResponseEntity<>(addingNewResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AddingNewResponse> addComment(
            @RequestBody CommentRequest commentRequest,
            Principal principal
            ) {
        AddingNewResponse addingNewResponse = addCommentService.addComment(principal, commentRequest);
        if (addingNewResponse.isResult()) {
            return ResponseEntity.ok(addingNewResponse);
        }
        else {
            return new ResponseEntity<>(addingNewResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<AddingNewResponse> moderatePost(
            @RequestBody ModerationRequest moderationRequest
            , Principal principal
    ){
        return ResponseEntity.ok(apiPostService.moderatePost(principal, moderationRequest));
    }


    @PostMapping(value = "/profile/my",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
//            consumes = {"multipart/form-data"}
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
            )
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> updateProfile(
            @ModelAttribute ProfileUpdateRequest profileUpdateRequest,
            Principal principal,
            HttpServletRequest request
            ) throws IOException {

        return ResponseEntity.ok(apiProfileService.profileEdit(profileUpdateRequest, principal, request));
    }

    @PostMapping(value = "/profile/my",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> updateProfile1(
            @RequestBody NoPhotoProfileUpdate noPhotoProfileUpdate,
            Principal principal,
            HttpServletRequest request
    ) throws IOException {
        return ResponseEntity.ok(apiProfileService.profileEditWithoutPhoto(noPhotoProfileUpdate, principal, request));
    }



    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public StatisticResponse getMyStatistics(Principal principal) {
        return apiStatisticService.getUserStatistic(principal);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<?> getAllUserStatistics(Principal principal) {
        if (settingsService.getStatisticStatus().equals("YES")) {
            return ResponseEntity.ok(apiStatisticService.getAllUserStatistic(principal));
        }
        else {
            if (principal == null) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
            else {
                return ResponseEntity.ok(apiStatisticService.getAllUserStatistic(principal));
            }
        }
    }

}
