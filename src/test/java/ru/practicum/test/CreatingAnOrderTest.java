package ru.practicum.test;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.practicum.model.User;

import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    private Map<String, Object> expectedBody;
    private Faker faker;

    public CreatingAnOrderTest(String testDescription, boolean useAuth, List<String> ingredients,
                               int expectedStatusCode, Map<String, Object> expectedBody) {
        this.testDescription = testDescription;
        this.useAuth = useAuth;
        this.ingredients = ingredients;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedBody = expectedBody;
    }

    @Parameterized.Parameters(name = "Тест \"{0}\": Ожидаемый код ответа {3}")
    public static Collection<Object[]> checkingTheResponseCodeWhenCreatingAnOrder() {
        return Arrays.asList(new Object[][]{
                {"Создание заказа с авторизацией и ингредиентами",
                        true,
                        Arrays.asList("61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa6f"),
                        200,
                        Map.of( "name", notNullValue(),
                                "order.number", notNullValue(),
                                "success", true
                                )},
                {"Создание заказа с ингредиентами без авторизации",
                        false,
                        Arrays.asList("61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa6f"),
                        401,
                        Map.of(
                                "success", false,
                                "message", "You should be authorised")},
                {"Создание заказа с авторизацией без ингредиентов",
                        true,
                        Collections.emptyList(),
                        400,
                        Map.of( "success", false,
                                "message", "Ingredient ids must be provided")},
                {"Создание заказа с авторизацией и неверным хешем ингредиентов",
                        true,
                        Arrays.asList(
                        "000000000000000000000000"),
                        500,
                        Map.of( "success", false,
                                "message", containsString("Internal Server Error"))}
        });
    }

    @Before
    public void setUp() {
        // Создать пользователя, если useAuth true
        if(useAuth) {
            faker = new Faker();
            user = generateRandomUser();
            // Отправить запрос на создание пользователя
            Response response = submitARequestToCreateAUser(user);
            // Отправить запрос на авторизацию пользователя
            sendARequestForUserAuthorizationInTheSystem(user);
            // Получить токен accessToken у пользователя
            this.accessToken = getAccessTokenFromUser(response);
        }
    }

    private User generateRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 8, true, true, true),
                faker.name().firstName()
        );
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