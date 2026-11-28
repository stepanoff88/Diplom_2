package ru.stellarburgers.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.stellarburgers.api.models.RegisteredUser;
import ru.stellarburgers.api.models.UserData;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@Epic("Stellar Burgers API")
@Feature("Авторизация пользователя")
@DisplayName("Логин пользователя")
class UserLoginTest extends BaseTest {

    @Test
    @DisplayName("Существующий пользователь успешно авторизуется")
    void loginExistingUser() {
        RegisteredUser user = createUser();

        Response response = userClient.login(user.getEmail(), user.getPassword());

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", not(emptyOrNullString()))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @ParameterizedTest(name = "Неверные данные: {0}")
    @ValueSource(strings = {"email", "password", "email и password"})
    @DisplayName("Логин с неверным логином и/или паролем возвращает ошибку")
    void loginWithInvalidCredentials(String invalidField) {
        RegisteredUser user = createUser();
        String email = user.getEmail();
        String password = user.getPassword();

        if ("email".equals(invalidField) || "email и password".equals(invalidField)) {
            email = UserData.uniqueEmail();
        }
        if ("password".equals(invalidField) || "email и password".equals(invalidField)) {
            password = "Wrong_" + UserData.uniquePassword();
        }

        Response response = userClient.login(email, password);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
