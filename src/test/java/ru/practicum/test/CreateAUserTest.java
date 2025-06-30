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

public class CreateAUserTest {
    private User user;
    private String accessToken;
    private Faker faker;


    @Before
    public void setUp() {
        faker = new Faker();
        user = generateRandomUser();
    }

    private User generateRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 8, true, true, true),
                faker.name().firstName()
        );
    }


    // Создание уникального пользователя
    @Test
    @DisplayName("Unique user creation")
    @Description("In the body of the request, specify the email address, password and name")
    public void uniqueUserCreationTest() {
        // 1. Отправить запрос на создание пользователя
        Response response = submitARequestToCreateAUser(user);
        // 2. Проверить, что ответ содержит accessToken и refreshToken
        Response responseToken = checkThatTheResponseContainsAccessTokenAndRefreshToken(response);
        // 3. Получить токен accessToken пользователя
        this.accessToken = getAccessTokenFromUser(responseToken);
    }


    // Создание пользователя, который уже был зарегистрирован
    @Test
    @DisplayName("Create a user who is already registered")
    @Description("When creating a user that has already been created, the system returns a 403 Forbidden response code.")
    public void createAUserWhoIsAlreadyRegisteredTest() {
        // 1. Отправить запрос на создание пользователя
        Response response = submitARequestToCreateAUser(user);
        // 2. Проверить, что ответ содержит accessToken и refreshToken
        Response responseToken = checkThatTheResponseContainsAccessTokenAndRefreshToken(response);
        // 3. Получить токен accessToken пользователя
        this.accessToken = getAccessTokenFromUser(responseToken);
        // 4. Отправить повторный запрос на создание пользователя
        Response repeatResponse = submitARequestToCreateAUser(user);
        // 5. Проверить, что ответ возвращается с кодом 403 Forbidden, а тело ответа содержит "User already exists"
        checkThatTheResponseBodyContainsUserAlreadyExists(repeatResponse);
    }


    // Создание пользователя без логина
    @Test
    @DisplayName("Creating user without login")
    @Description("If you do not pass the login in the request, the code 403 Forbidden will be returned")
    public void creatingUserWithoutLoginTest() {
        // 1. Создать объект класса User со значением поля Email null
        user = new User(null, "password", "User22");
        // 2. Отправить запрос на создание пользователя без логина
        Response response = submitARequestToCreateAUser(user);
        // 3. Проверить, что ответ возвращается с кодом 403 Forbidden,
        // а тело ответа содержит "Email, password and name are required fields"
        checkThatTheResponseBodyContainsEmailPasswordAndNameAreRequiredFields(response);
    }


    // Создание пользователя без пароля
    @Test
    @DisplayName("Creating user without password")
    @Description("If you do not pass the password in the request, the code 403 Forbidden will be returned")
    public void creatingUserWithoutPasswordTest() {
        // 1. Создать объект класса User со значением поля password null
        user = new User("user22@yandex.ru", null, "User22");
        // 2. Отправить запрос на создание пользователя без логина
        Response response = submitARequestToCreateAUser(user);
        // 3. Проверить, что ответ возвращается с кодом 403 Forbidden,
        // а тело ответа содержит "Email, password and name are required fields"
        checkThatTheResponseBodyContainsEmailPasswordAndNameAreRequiredFields(response);
    }


    // // Создание пользователя без имени
    @Test
    @DisplayName("Creating user without name")
    @Description("If you do not pass the name in the request, the code 403 Forbidden will be returned")
    public void creatingUserWithoutNameTest() {
        // 1. Создать объект класса User со значением поля password null
        user = new User("user22@yandex.ru", "password", null);
        // 2. Отправить запрос на создание пользователя без логина
        Response response = submitARequestToCreateAUser(user);
        // 3. Проверить, что ответ возвращается с кодом 403 Forbidden,
        // а тело ответа содержит "Email, password and name are required fields"
        checkThatTheResponseBodyContainsEmailPasswordAndNameAreRequiredFields(response);
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