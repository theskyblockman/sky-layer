package fr.theskyblockman.skylayer.player_identity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerIdentityOverrider implements Listener {
    @EventHandler
    public void onPlayerSendMessage(AsyncPlayerChatEvent event) {
        event.setFormat(PlayerIdentityLayer.manager.playerMessage(event.getPlayer(), event.getMessage()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setDisplayName(PlayerIdentityLayer.manager.playerDisplayName(event.getPlayer()));
    }
}
