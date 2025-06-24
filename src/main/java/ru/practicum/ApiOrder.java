package ru.practicum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiOrder {

    private static List<String> ingredients;

    // Метод для шага "Отправить запрос на создание заказа"
    @Step("Send a request to create an order")
    public static Response sendARequestToCreateAnOrder(String accessToken, List<String> ingredients) {
        RequestSpecification request = given().spec(ApiSpec.getBaseSpec()).body(new Order(ingredients));
        if (accessToken != null) {
            request.header("Authorization", "Bearer " + accessToken);
        }
        return request.when().post(Endpoints.CREATING_AN_ORDER);
    }
}