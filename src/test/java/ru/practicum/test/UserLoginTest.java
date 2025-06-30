package ru.practicum.test;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.model.User;

import static ru.practicum.ApiUser.*;

public class UserLoginTest {

    private User user;
    private String accessToken;
    private Faker faker;

    @Before
    public void setUp() {
        faker = new Faker();
        user = generateRandomUser();
        Response response = submitARequestToCreateAUser(user);
        Response responseToken = checkThatTheResponseContainsAccessTokenAndRefreshToken(response);
        this.accessToken = getAccessTokenFromUser(responseToken);
    }

    private User generateRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 8, true, true, true),
                faker.name().firstName()
        );
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
        // 1. Корректные данные password и name созданного пользователя
        String validPassword = user.getPassword();
        String validName = user.getName();
        // 2. Создать объект класса User с другим значением поля Email
        user = new User("user2@yandex.ru", validPassword, validName);
        // 3. Отправить запрос на авторизацию пользователя в системе с неправильным email
        Response response = sendARequestForUserAuthorizationInTheSystem(user);
        // 4. Проверить, что ответ возвращается с кодом 401 Unauthorized
        checkThatTheResponseIsReturnedWithTheCode401Unauthorized(response);
    }


    // Вход с неверным паролем
    @Test
    @DisplayName("Login with invalid password")
    @Description("Если пароль недействителен, будет возвращен код ответа 401 Unauthorized.")
    public void loginWithInvalidPasswordTest() {
        // 1. Корректные данные email и name созданного пользователя
        String validEmail = user.getEmail();
        String validName = user.getName();
        // 2. Создать объект класса User с другим значением поля password
        user = new User(validEmail, "passsword", validName);
        // 3. Отправить запрос на авторизацию пользователя в системе с неправильным password
        Response response = sendARequestForUserAuthorizationInTheSystem(user);
        // 4. Проверить, что ответ возвращается с кодом 401 Unauthorized
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