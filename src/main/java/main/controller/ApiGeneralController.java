package main.controller;


import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.request.NewSettingsRequest;
import main.api.response.*;
import main.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, ApiTagsService apiTagsService, ApiCalendarService apiCalendarService, ApiPostService apiPostService, AddCommentService addCommentService, ApiStatisticService apiStatisticService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.apiTagsService = apiTagsService;
        this.apiCalendarService = apiCalendarService;
        this.apiPostService = apiPostService;
        this.addCommentService = addCommentService;
        this.apiStatisticService = apiStatisticService;
    }

    @GetMapping("/init")
    private InitResponse initResponse() {
        return initResponse;
    }


    @GetMapping("/settings")
    public GlobalSettingsResponse settingsGet () {
        return settingsService.getSettingsResponse();
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

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<AddingNewResponse> addComment(
            @RequestBody CommentRequest commentRequest,
            Principal principal
            ) {
        return ResponseEntity.ok(addCommentService.addComment(principal, commentRequest));
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<AddingNewResponse> moderatePost(
            @RequestBody ModerationRequest moderationRequest
            , Principal principal
    ){
        return ResponseEntity.ok(apiPostService.moderatePost(principal, moderationRequest));
    }

    @PostMapping("/profile/my")
    public void updateProfile() {

    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public StatisticResponse getMyStatistics(Principal principal) {
        return apiStatisticService.getUserStatistic(principal);
    }

    @GetMapping("/statistics/all")
    public StatisticResponse getAllUserStatistics(Principal principal) {
        return apiStatisticService.getAllUserStatistic(principal);
    }

}
