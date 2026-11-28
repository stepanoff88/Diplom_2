package ru.stellarburgers.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.stellarburgers.api.models.RegisteredUser;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Stellar Burgers API")
@Feature("Создание заказа")
@DisplayName("Создание заказа")
class OrderCreationTest extends BaseTest {

    private List<String> ingredientIds;

    @BeforeEach
    void loadIngredients() {
        ingredientIds = orderClient.getIngredientIds();
        assertTrue(ingredientIds.size() >= 2, "Для тестов нужно минимум два ингредиента");
    }

    @Test
    @DisplayName("Авторизованный пользователь создаёт заказ")
    void createOrderWithAuthorization() {
        RegisteredUser user = createUser();

        Response response = orderClient.createOrder(
                user.getAccessToken(), ingredientIds.subList(0, 1));

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Заказ без авторизации создаётся")
    void createOrderWithoutAuthorization() {
        Response response = orderClient.createOrder(ingredientIds.subList(0, 1));

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Заказ создаётся с переданными ингредиентами")
    void createOrderWithIngredients() {
        RegisteredUser user = createUser();
        List<String> chosenIngredients = ingredientIds.subList(0, 2);

        Response response = orderClient.createOrder(user.getAccessToken(), chosenIngredients);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients._id",
                        containsInAnyOrder(chosenIngredients.toArray(new String[0])))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Заказ без ингредиентов не создаётся")
    void createOrderWithoutIngredients() {
        RegisteredUser user = createUser();

        Response response = orderClient.createOrder(
                user.getAccessToken(), Collections.emptyList());

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хешем ингредиента не создаётся")
    void createOrderWithInvalidIngredientHash() {
        RegisteredUser user = createUser();

        Response response = orderClient.createOrder(
                user.getAccessToken(), List.of("not-a-valid-ingredient-hash"));

        response.then()
                .statusCode(500)
                .body(containsString("Internal Server Error"));
    }
}
