package fr.theskyblockman.skylayer.player_identity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface PlayerIdentityManager {
    String playerPrefix(Player player);
    String playerSuffix(Player player);
    String playerName  (Player player);

    /**
     * Resolves the ID of a player for the given player. This helper function is made to work in a stable way with shared teams
     * @param player The player to resolve the ID for
     * @return The ID of the player
     */
    @Nullable
    default String resolveIdForPlayer(Player player) {
        return player.getUniqueId().toString();
    }

    default String playerDisplayName(Player player) {
        return    ChatColor.RESET
                + playerPrefix(player)
                + ChatColor.RESET
                + playerName(player)
                + ChatColor.RESET
                + playerSuffix(player);
    }

    default String playerMessage(Player player, String message) {
        return playerDisplayName(player) + ChatColor.RESET + ": " + message;
    }
}
