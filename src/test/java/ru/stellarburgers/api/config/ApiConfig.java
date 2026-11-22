package ru.stellarburgers.api.config;

public final class ApiConfig {

    public static final String BASE_URL = System.getProperty(
            "base.url", "https://qa-stellarburgers.education-services.ru");
    public static final String BASE_PATH = "/api";

    private ApiConfig() {
    }
}
