package ru.stellarburgers.api.models;

import java.util.UUID;

public class UserData {

    private final String email;
    private final String password;
    private final String name;

    public UserData(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static UserData random() {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return new UserData(
                "autotest_" + id + "@example.com",
                "Passw0rd_" + id,
                "Autotest " + id);
    }

    public static String uniqueEmail() {
        return random().getEmail();
    }

    public static String uniquePassword() {
        return random().getPassword();
    }

    public static String uniqueName() {
        return random().getName();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
