package ru.practicum;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpec {

    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://stellarburgers.nomoreparties.site")
                .addHeader("Content-Type", "application/json")
                .build();
    }
}