package ru.stellarburgers.api.models;

public class RegisteredUser {

    private final UserData data;
    private final String accessToken;

    public RegisteredUser(UserData data, String accessToken) {
        this.data = data;
        this.accessToken = accessToken;
    }

    public UserData getData() {
        return data;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmail() {
        return data.getEmail();
    }

    public String getPassword() {
        return data.getPassword();
    }

    public String getName() {
        return data.getName();
    }
}
