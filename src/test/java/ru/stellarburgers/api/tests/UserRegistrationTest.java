package ru.stellarburgers.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.stellarburgers.api.clients.UserClient;
import ru.stellarburgers.api.models.RegisteredUser;
import ru.stellarburgers.api.models.UserData;

import java.util.Map;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@Epic("Stellar Burgers API")
@Feature("Создание пользователя")
@DisplayName("Создание пользователя")
class UserRegistrationTest extends BaseTest {

    @Test
    @DisplayName("Уникальный пользователь успешно создаётся")
    void createUniqueUser() {
        UserData userData = UserData.random();

        Response response = userClient.register(UserClient.toBody(userData));
        RegisteredUser registeredUser = trackUser(userData, response);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(registeredUser.getEmail()))
                .body("user.name", equalTo(registeredUser.getName()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Пользователь с уже зарегистрированным email не создаётся")
    void createAlreadyRegisteredUser() {
        RegisteredUser existingUser = createUser();

        Response response = userClient.register(UserClient.toBody(existingUser.getData()));

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @ParameterizedTest(name = "Без обязательного поля {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Пользователь без одного из обязательных полей не создаётся")
    void createUserWithoutRequiredField(String missingField) {
        Map<String, Object> body = UserClient.toBody(UserData.random());
        body.remove(missingField);

        Response response = userClient.register(body);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
