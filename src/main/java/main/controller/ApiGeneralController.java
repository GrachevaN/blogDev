package main.controller;


import main.api.response.ApiGetTagsResponse;
import main.api.response.GlobalSettingsResponse;
import main.api.response.InitResponse;
import main.service.ApiTagsService;
import main.service.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {


    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final ApiTagsService apiTagsService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, ApiTagsService apiTagsService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.apiTagsService = apiTagsService;
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

}
