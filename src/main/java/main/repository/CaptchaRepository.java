package main.repository;

import main.model.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface CaptchaRepository extends JpaRepository <CaptchaCodes, Integer> {


    List<CaptchaCodes> findCaptchaCodesByTimeBefore(Timestamp time);

    CaptchaCodes findBySecretCode(String secret);


}
