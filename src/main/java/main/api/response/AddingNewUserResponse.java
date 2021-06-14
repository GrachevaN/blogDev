package main.api.response;

public class AddingNewUserResponse {

    private boolean result;
    private String email;
    private String name;
    private String password;
    private String captcha;

    public AddingNewUserResponse() {
        this.result = true;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

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


}
