package main.controller;


import main.api.response.*;
import main.service.ApiCalendarService;
import main.service.ApiPostService;
import main.service.ApiTagsService;
import main.service.SettingsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, ApiTagsService apiTagsService, ApiCalendarService apiCalendarService, ApiPostService apiPostService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.apiTagsService = apiTagsService;
        this.apiCalendarService = apiCalendarService;
        this.apiPostService = apiPostService;
    }

    @GetMapping("/init")
    private InitResponse initResponse() {
        return initResponse;
    }


    @GetMapping("/settings")
    public GlobalSettingsResponse settingsGet () {
        return settingsService.getSettingsResponse();
    }

//    @GetMapping("/tag/{tagName}")
    @GetMapping("/tag")
    public ApiGetTagsResponse getTags (
            @RequestParam(defaultValue = "") String tagName
//            @PathVariable String tagName
    ) {
        return apiTagsService.getTags(tagName);
    }

    @GetMapping("/calendar")
    public ApiCalendarResponse getPublicationCountPerYear(
            @RequestParam(defaultValue = "0") int year
    ) {
        return apiCalendarService.getApiCalendar(year);
    }




}
