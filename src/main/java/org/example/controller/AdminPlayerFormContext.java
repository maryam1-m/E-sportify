package org.example.controller;

/** Passes selected {@code player_id} from admin list into {@link EditPlayerController}. */
public final class AdminPlayerFormContext {

    private static int editPlayerId = -1;

    private AdminPlayerFormContext() {
    }

    public static void setEditPlayerId(int playerId) {
        editPlayerId = playerId;
    }

    public static int getEditPlayerId() {
        return editPlayerId;
    }

    public static void clearEditPlayerId() {
        editPlayerId = -1;
    }

    public static boolean hasEditPlayerId() {
        return editPlayerId > 0;
    }
}
