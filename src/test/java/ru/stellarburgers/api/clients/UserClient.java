package ru.stellarburgers.api.clients;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.stellarburgers.api.models.UserData;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Создание пользователя")
    public Response register(Map<String, Object> body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response login(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/login");
    }

    @Step("Получение данных пользователя")
    public Response getUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get("/auth/user");
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String accessToken, Map<String, Object> fields) {
        return given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(fields)
                .when()
                .patch("/auth/user");
    }

    @Step("Обновление данных пользователя без авторизации")
    public Response updateUserWithoutAuth(Map<String, Object> fields) {
        return given()
                .contentType(ContentType.JSON)
                .body(fields)
                .when()
                .patch("/auth/user");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }

    public static Map<String, Object> toBody(UserData user) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", user.getEmail());
        body.put("password", user.getPassword());
        body.put("name", user.getName());
        return body;
    }
}
