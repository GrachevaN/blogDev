package main.repository;

import main.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingsRepository extends JpaRepository <Settings, Integer> {

}
