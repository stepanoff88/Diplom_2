package ru.stellarburgers.api.clients;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrderClient {

    @Step("Получение списка ингредиентов")
    public List<String> getIngredientIds() {
        return given()
                .when()
                .get("/ingredients")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .jsonPath()
                .getList("data._id", String.class);
    }

    @Step("Создание заказа с авторизацией")
    public Response createOrder(String accessToken, List<String> ingredientIds) {
        return given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderBody(ingredientIds))
                .when()
                .post("/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrder(List<String> ingredientIds) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderBody(ingredientIds))
                .when()
                .post("/orders");
    }

    @Step("Получение заказов авторизованного пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get("/orders");
    }

    @Step("Получение заказов без авторизации")
    public Response getUserOrders() {
        return given()
                .when()
                .get("/orders");
    }

    private static Map<String, Object> orderBody(List<String> ingredientIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);
        return body;
    }
}
