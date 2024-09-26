package ru.korundm.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс для хранения информации о пользователе из Principal
 * @author pakhunov_an
 * Date:   07.02.2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggedUser {

    private static final ThreadLocal<String> USER_HOLDER = new ThreadLocal<>();

    public static void logIn(String user) {
        USER_HOLDER.set(user);
    }

    public static void logOut() {
        USER_HOLDER.remove();
    }

    public static String get() {
        return USER_HOLDER.get();
    }
}