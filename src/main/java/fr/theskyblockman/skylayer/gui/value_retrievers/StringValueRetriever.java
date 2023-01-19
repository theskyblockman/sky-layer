package fr.theskyblockman.skylayer.gui.value_retrievers;

import fr.theskyblockman.skylayer.gui.GUILayer;
import fr.theskyblockman.skylayer.gui.RetrievedValueExecutor;
import fr.theskyblockman.skylayer.gui.ValueSetter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StringValueRetriever implements Listener, ValueSetter<String> {

    @Override
    public void retrieveValue(Player player, RetrievedValueExecutor<String> onFound) {
        player.getServer().getPluginManager().registerEvents(this, GUILayer.moduleInitializer);
        player.closeInventory();
        this.currentPlayer = player;
        this.onFound = onFound;
    }
    private Player currentPlayer;
    private RetrievedValueExecutor<String> onFound;
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if(currentPlayer != null && event.getPlayer().getUniqueId() == currentPlayer.getUniqueId()) {
            onFound.onValueRetrieved(event.getMessage());
            event.setCancelled(true);
            HandlerList.unregisterAll(this);
        }
    }
}
