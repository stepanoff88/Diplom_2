package ru.stellarburgers.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.stellarburgers.api.models.RegisteredUser;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Stellar Burgers API")
@Feature("Получение заказов пользователя")
@DisplayName("Получение заказов конкретного пользователя")
class UserOrdersTest extends BaseTest {

    private List<String> ingredientIds;

    @BeforeEach
    void loadIngredients() {
        ingredientIds = orderClient.getIngredientIds();
        assertTrue(ingredientIds.size() >= 2, "Для тестов нужно минимум два ингредиента");
    }

    @Test
    @DisplayName("Авторизованный пользователь получает свои заказы")
    void getUserOrdersWithAuthorization() {
        RegisteredUser user = createUser();

        Response createdOrder = orderClient.createOrder(
                user.getAccessToken(), ingredientIds.subList(0, 2));
        createdOrder.then().statusCode(200);
        int orderNumber = createdOrder.jsonPath().getInt("order.number");

        Response response = orderClient.getUserOrders(user.getAccessToken());

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders.number", hasItem(orderNumber))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Неавторизованный пользователь не получает заказы")
    void getUserOrdersWithoutAuthorization() {
        Response response = orderClient.getUserOrders();

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
