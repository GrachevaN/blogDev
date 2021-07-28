package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorsDTO {

    private String email;
    private String name;
    private String password;
    private String captcha;
    private String title;
    private String text;
    private String photo;
    private String code;

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

    public void setPhoto() {this.photo = "Фото слишком большое, нужно не более 5 Мб";}

    public void setCode() {
        this.code = "Ссылка для восстановления пароля устарела. <a href= \"/auth/restore\">Запросить ссылку снова</a>";
    }
}
