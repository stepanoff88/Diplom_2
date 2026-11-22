package ru.stellarburgers.api.tests;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.stellarburgers.api.clients.OrderClient;
import ru.stellarburgers.api.clients.UserClient;
import ru.stellarburgers.api.config.ApiConfig;
import ru.stellarburgers.api.models.RegisteredUser;
import ru.stellarburgers.api.models.UserData;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

/**
 * Базовый класс: настройка RestAssured и удаление созданных тестовых пользователей.
 */
public abstract class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final OrderClient orderClient = new OrderClient();

    private final List<RegisteredUser> usersToDelete = new ArrayList<>();

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
        RestAssured.basePath = ApiConfig.BASE_PATH;
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void deleteCreatedUsers() {
        usersToDelete.forEach(user -> {
            if (user.getAccessToken() != null) {
                userClient.deleteUser(user.getAccessToken());
            }
        });
        usersToDelete.clear();
    }

    @Step("Создать тестового пользователя")
    protected RegisteredUser createUser() {
        UserData userData = UserData.random();
        Response response = userClient.register(UserClient.toBody(userData));
        RegisteredUser registeredUser = trackUser(userData, response);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        return registeredUser;
    }

    @Step("Запомнить пользователя для удаления после теста")
    protected RegisteredUser trackUser(UserData userData, Response registrationResponse) {
        RegisteredUser registeredUser = new RegisteredUser(
                userData, registrationResponse.jsonPath().getString("accessToken"));
        usersToDelete.add(registeredUser);
        return registeredUser;
    }
}
