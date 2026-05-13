package org.example.session;

/** Holds the logged-in AppUser (PLAYER or ADMIN). {@code playerId} is 0 when not linked to a player. */
public final class UserSession {

    private static int userId;
    private static int playerId;
    private static String name;
    private static String email;
    private static String role;

    private UserSession() {
    }

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

    public static boolean isLoggedIn() {
        return userId > 0 && role != null && !role.isBlank();
    }

    public static boolean isAdmin() {
        return isLoggedIn() && "ADMIN".equalsIgnoreCase(role);
    }

    public static boolean isPlayer() {
        return isLoggedIn() && "PLAYER".equalsIgnoreCase(role);
    }

    public static void clearSession() {
        userId = 0;
        playerId = 0;
        name = null;
        email = null;
        role = null;
    }
}
