package org.example.session;

public class UserSession {

    private static int userId;
    private static int playerId;
    private static String name;
    private static String email;
    private static String role;

    public static void setSession(int userId, int playerId, String name, String email, String role) {
        UserSession.userId = userId;
        UserSession.playerId = playerId;
        UserSession.name = name;
        UserSession.email = email;
        UserSession.role = role;
    }

    public static int getUserId() {
        return userId;
    }

    public static int getPlayerId() {
        return playerId;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        userId = 0;
        playerId = 0;
        name = null;
        email = null;
        role = null;
    }
}