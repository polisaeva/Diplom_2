package ru.practicum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.model.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ApiUser {

    //Метод для шага "Отправить запрос на создание пользователя"
    @Step("Send POST request to /api/auth/register")
    public static Response submitARequestToCreateAUser(User user) {
        Response response = given().spec(ApiSpec.getBaseSpec()).body(user).when().post(Endpoints.CREATE_A_USER);
        return response;
    }


    //Метод для шага "Проверить, что ответ содержит accessToken и refreshToken"
    @Step("Check that the response contains accessToken and refreshToken")
    public static Response checkThatTheResponseContainsAccessTokenAndRefreshToken(Response response) {
        response.then().body("accessToken", notNullValue()).body("refreshToken", notNullValue());
        return response;
    }


    //Метод для шага "Получить токен accessToken пользователя"
    @Step("Get accessToken from user")
    public static String getAccessTokenFromUser(Response response) {
        String accessToken = response.jsonPath().getString("accessToken").split(" ")[1];
        return accessToken;
    }


    //Метод для шага "Проверить, что ответ возвращается с кодом 403 Forbidden, а тело ответа содержит "User already
    // exists"
    @Step("Check that the response body contains User already exists")
    public static void checkThatTheResponseBodyContainsUserAlreadyExists(Response response) {
        response.then().statusCode(403).body("success", equalTo(false)).body("message",
                equalTo("User already exists"));
    }


    //Метод для шага "Проверить, что ответ возвращается с кодом 403 Forbidden, а тело ответа содержит "Email, password
    // and name are required fields"
    @Step("Check that the response body contains email, password and name are required fields")
    public static void checkThatTheResponseBodyContainsEmailPasswordAndNameAreRequiredFields(Response response) {
        response.then().statusCode(403).body("success", equalTo(false)).body("message",
                equalTo("Email, password and name are required fields"));
    }


    //Метод для шага "Отправить запрос на авторизацию пользователя в системе"
    @Step("Send a request for user authorization in the system")
    public static Response sendARequestForUserAuthorizationInTheSystem(User user) {
        Response response = given().spec(ApiSpec.getBaseSpec()).body(user).when().post(Endpoints.USER_LOGIN);
        return response;
    }


    //Метод для шага "Проверить, что ответ возвращается с кодом 401 Unauthorized"
    @Step("Check that the response is returned with the code 401 Unauthorized")
    public static void checkThatTheResponseIsReturnedWithTheCode401Unauthorized(Response response) {
        response.then().statusCode(401).body("success", equalTo(false)).body("message",
                equalTo("email or password are incorrect"));
    }


    //Метод для шага "Удалить пользователя из системы"
    @Step("Remove user from the system")
    public static Response removeUserFromTheSystem(String accessToken) {
        Response response = given().spec(ApiSpec.getBaseSpec()).header("Authorization", "Bearer " + accessToken)
                .when().delete(Endpoints.DELETING_A_USER);
        return response;
    }


    //Метод для шага "Проверить, что ответ возвращается с кодом 202 Accepted"
    @Step("Check that the response is returned with code 202 Accepted")
    public static void checkThatTheResponseIsReturnedWithCode202Accepted(Response response) {
        response.then().statusCode(202);
    }

}