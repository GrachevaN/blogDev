package main.controller;


import main.api.response.ApiCalendarResponse;
import main.api.response.ApiGetTagsResponse;
import main.api.response.GlobalSettingsResponse;
import main.api.response.InitResponse;
import main.service.ApiCalendarService;
import main.service.ApiTagsService;
import main.service.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final ApiTagsService apiTagsService;
    private final ApiCalendarService apiCalendarService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, ApiTagsService apiTagsService, ApiCalendarService apiCalendarService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.apiTagsService = apiTagsService;
        this.apiCalendarService = apiCalendarService;
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
