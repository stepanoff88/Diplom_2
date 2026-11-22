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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@Epic("Stellar Burgers API")
@Feature("Изменение данных пользователя")
@DisplayName("Изменение данных пользователя")
class UserUpdateTest extends BaseTest {

    @Test
    @DisplayName("Авторизованный пользователь может изменить email")
    void updateEmailWithAuthorization() {
        RegisteredUser user = createUser();
        String newEmail = UserData.uniqueEmail();

        Response response = userClient.updateUser(user.getAccessToken(), Map.of("email", newEmail));

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(user.getName()));

        userClient.getUser(user.getAccessToken())
                .then()
                .statusCode(200)
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить пароль")
    void updatePasswordWithAuthorization() {
        RegisteredUser user = createUser();
        String newPassword = UserData.uniquePassword();

        Response response = userClient.updateUser(user.getAccessToken(), Map.of("password", newPassword));

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()));

        userClient.login(user.getEmail(), newPassword)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        userClient.login(user.getEmail(), user.getPassword())
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить имя")
    void updateNameWithAuthorization() {
        RegisteredUser user = createUser();
        String newName = UserData.uniqueName();

        Response response = userClient.updateUser(user.getAccessToken(), Map.of("name", newName));

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName))
                .body("user.email", equalTo(user.getEmail()));

        userClient.getUser(user.getAccessToken())
                .then()
                .statusCode(200)
                .body("user.name", equalTo(newName));
    }

    @ParameterizedTest(name = "Изменение поля {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Неавторизованный пользователь не может изменить данные")
    void updateUserWithoutAuthorization(String field) {
        RegisteredUser user = createUser();

        Response response = userClient.updateUserWithoutAuth(changesFor(field));

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));

        userClient.getUser(user.getAccessToken())
                .then()
                .statusCode(200)
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        userClient.login(user.getEmail(), user.getPassword())
                .then()
                .statusCode(200);
    }

    private Map<String, Object> changesFor(String field) {
        Map<String, Object> changes = new HashMap<>();
        switch (field) {
            case "email":
                changes.put("email", UserData.uniqueEmail());
                break;
            case "password":
                changes.put("password", UserData.uniquePassword());
                break;
            case "name":
                changes.put("name", UserData.uniqueName());
                break;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
        return changes;
    }
}
