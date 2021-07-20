package main.service;

import main.api.errs.NoAuthoraizedExc;
import main.api.request.NewSettingsRequest;
import main.api.response.GlobalSettingsResponse;
import main.model.User;
import main.repository.GlobalSettingsRepository;
import main.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository settingsRepository;

    private AuthCheckService authCheckService;

    public SettingsService(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }


    public GlobalSettingsResponse getSettingsResponse () {
        GlobalSettingsResponse settingsResponse = new GlobalSettingsResponse();
        List <Settings> settings = settingsRepository.findAll();
        settings.forEach(x -> {
            switch (x.getCode()) {
                case ("MULTIUSER_MODE"):
                    settingsResponse.setMultiuserMode(getBooleanValue(x.getValue()));
                    break;
                case ("POST_PREMODERATION"):
                    settingsResponse.setPostPremoderation(getBooleanValue(x.getValue()));
                    break;
                case ("STATISTICS_IS_PUBLIC"):
                    settingsResponse.setStatisticIsPublic(getBooleanValue(x.getValue()));
                    break;
            }
        });
        return settingsResponse;
    }

    public void putSettings(Principal principal, NewSettingsRequest newSettingsRequest) {
        User user = authCheckService.getAuthUser(principal);
        if (!user.equals(null)){
            if (user.getIs_moderator() == 1) {
                Settings settings1 = settingsRepository.findByCode("MULTIUSER_MODE");
                settings1.setValue(getStringValue(newSettingsRequest.isMultiuserMode()));
                Settings settings2 = settingsRepository.findByCode("POST_PREMODERATION");
                settings2.setValue(getStringValue(newSettingsRequest.isPostPremoderation()));
                Settings settings3 = settingsRepository.findByCode("STATISTICS_IS_PUBLIC");
                settings3.setValue(getStringValue(newSettingsRequest.isStatisticIsPublic()));
                settingsRepository.save(settings1);
                settingsRepository.save(settings2);
                settingsRepository.save(settings3);
            }
        }
        else {
            throw new NoAuthoraizedExc();
        }
    }


    private boolean getBooleanValue(String val) {
        switch (val) {
            case ("YES"):
                return true;
            case ("NO"):
                return false;
        }
        return false;
    }

    private String getStringValue(boolean val) {
        if (val) {
            return "YES";
        } else if (!(val)) {
            return "NO";
        }
        return "NO";
    }

}
