package ru.practicum.test;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.User;

import static ru.practicum.ApiUser.*;

public class UserLoginTest {

    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = new User("user22@yandex.ru", "password", "User22");
        Response response = submitARequestToCreateAUser(user);
        Response responseToken = checkThatTheResponseContainsAccessTokenAndRefreshToken(response);
        this.accessToken = getAccessTokenFromUser(responseToken);
    }


    // Вход под существующим пользователем
    @Test
    @DisplayName("Login as an existing user")
    @Description("When logging in as an existing user, the response body returns code 200 OK")
    public void loginAsAnExistingUserTest() {
        // 1. Отправить запрос на авторизацию пользователя в системе
        Response response = sendARequestForUserAuthorizationInTheSystem(user);
        // 2. Проверить, что ответ содержит accessToken и refreshToken
        checkThatTheResponseContainsAccessTokenAndRefreshToken(response);
    }

    // Вход с неверным адресом электронной почты
    @Test
    @DisplayName("Login with invalid email")
    @Description("If the email is invalid, a 401 Unauthorized response code will be returned")
    public void loginWithInvalidEmailTest() {
        // 1. Создать объект класса User со другим значением поля Email
        user = new User("user2@yandex.ru", "password", "User22");
        // 2. Отправить запрос на авторизацию пользователя в системе с неправильным email
        Response response = sendARequestForUserAuthorizationInTheSystem(user);
        // 3. Проверить, что ответ возвращается с кодом 401 Unauthorized
        checkThatTheResponseIsReturnedWithTheCode401Unauthorized(response);
    }


    // Вход с неверным паролем
    @Test
    @DisplayName("Login with invalid password")
    @Description("Если пароль недействителен, будет возвращен код ответа 401 Unauthorized.")
    public void loginWithInvalidPasswordTest() {
        // 1. Создать объект класса User со другим значением поля password
        user = new User("user22@yandex.ru", "passsword", "User22");
        // 2. Отправить запрос на авторизацию пользователя в системе с неправильным password
        Response response = sendARequestForUserAuthorizationInTheSystem(user);
        // 3. Проверить, что ответ возвращается с кодом 401 Unauthorized
        checkThatTheResponseIsReturnedWithTheCode401Unauthorized(response);
    }


    @After
    //Удаление пользователя из системы
    public void cleanUp() {
        if (this.accessToken != null) {
            Response response = removeUserFromTheSystem(this.accessToken);
            checkThatTheResponseIsReturnedWithCode202Accepted(response);
        }
    }

}