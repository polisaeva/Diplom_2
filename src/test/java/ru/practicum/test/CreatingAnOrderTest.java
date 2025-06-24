package ru.practicum.test;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.practicum.User;

import java.util.*;

import static ru.practicum.ApiOrder.sendARequestToCreateAnOrder;
import static ru.practicum.ApiUser.*;
import static ru.practicum.ApiUser.checkThatTheResponseIsReturnedWithCode202Accepted;

@RunWith(Parameterized.class)
public class CreatingAnOrderTest {

    private User user;
    private String accessToken;
    private boolean useAuth;
    private List<String> ingredients;
    private int expectedStatusCode;
    private String testDescription;

    public CreatingAnOrderTest(String testDescription, boolean useAuth, List<String> ingredients,
                               int expectedStatusCode) {
        this.testDescription = testDescription;
        this.useAuth = useAuth;
        this.ingredients = ingredients;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters(name = "Тест \"{0}\": Ожидаемый код ответа {3}")
    public static Collection<Object[]> checkingTheResponseCodeWhenCreatingAnOrder() {
        return Arrays.asList(new Object[][]{
                {"Создание заказа с авторизацией и ингредиентами", true, Arrays.asList(
                        "61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa6f"), 200},
                {"Создание заказа с ингредиентами без авторизации", false, Arrays.asList(
                        "61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa6f"), 401},
                {"Создание заказа с авторизацией без ингредиентов", true, Collections.emptyList(), 400},
                {"Создание заказа с авторизацией и неверным хешем ингредиентов", true, Arrays.asList(
                        "000000000000000000000000"), 500}
        });
    }

    @Before
    public void setUp() {
        // Создать пользователя, если useAuth true
        if(useAuth) {
            user = new User("user22@yandex.ru", "password", "User22");
            // Отправить запрос на создание пользователя
            Response response = submitARequestToCreateAUser(user);
            // Отправить запрос на авторизацию пользователя
            sendARequestForUserAuthorizationInTheSystem(user);
            // Получить токен accessToken у пользователя
            this.accessToken = getAccessTokenFromUser(response);
        }
    }

    @Test
    @DisplayName("The expected response code is returned")
    @Description("When creating an order the response is returned with the expected code")
    public void whenCreatingAnOrderTheResponseIsReturnedWithTheExpectedCode() {
        // 1. Отправить запрос на создание заказа
        Response response = sendARequestToCreateAnOrder(
                useAuth ? this.accessToken : null, this.ingredients);
        // 2. Проверить, что ответ возвращается с ожидаемым кодом
        response.then().statusCode(expectedStatusCode);
    }

    @After
    //Удаление пользователя из системы
    public void cleanUp() {
        if (useAuth && this.accessToken != null) {
            Response response = removeUserFromTheSystem(this.accessToken);
            checkThatTheResponseIsReturnedWithCode202Accepted(response);
        }
    }
}