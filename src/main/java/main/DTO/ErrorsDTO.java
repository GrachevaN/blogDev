package main.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsDTO {

    private String email;
    private String name;
    private String password;
    private String captcha;
    private String title;
    private String text;

    public void setEmailError() {
        this.email = "Этот e-mail уже зарегистрирован";
    }

    public void setNameError() {
        this.name = "Имя указано неверно";
    }

    public void setPasswordError() {
        this.password = "Пароль короче 6-ти символов";
    }

    public void setCaptchaError() {
        this.captcha = "Код с картинки введён неверно";
    }

    public void setTitleError() {
        this.title = "Заголовок не установлен";
    }

    public void setTextError() {
        this.text = "Текст публикации слишком короткий";
    }

    public void setTextCommentError() {
        this.text = "Текст комментария не задан или слишком короткий";
    }

}
