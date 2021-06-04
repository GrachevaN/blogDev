package main.service;

import main.api.response.GlobalSettingsResponse;
import main.repository.GlobalSettingsRepository;
import main.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository settingsRepository;


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
                case ("STATISTIC_IS_PUBLIC"):
                    settingsResponse.setStatisticIsPublic(getBooleanValue(x.getValue()));
                    break;
            }
        });
        return settingsResponse;
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

}
