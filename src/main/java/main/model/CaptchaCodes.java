package main.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Timestamp theTime;

//    java.util.Date date = new Date();
//    Object param = new java.sql.Timestamp(date.getTime());
//// The JDBC driver knows what to do with a java.sql type:
//    preparedStatement.setObject(param);


    @NotNull
    private String code;

    @NotNull
    @Column(name = "secret_code")
    private String secretCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTheTime() {
        return theTime;
    }

    public void setTheTime(Timestamp theTime) {
        this.theTime = theTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
