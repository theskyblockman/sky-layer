package fr.theskyblockman.skylayer.gui.value_retrievers;

import fr.theskyblockman.skylayer.gui.GUILayer;
import fr.theskyblockman.skylayer.gui.RetrievedValueExecutor;
import fr.theskyblockman.skylayer.gui.ValueSetter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LocationValueRetriever implements Listener, ValueSetter<Location> {
    private Player player;
    private RetrievedValueExecutor<Location> onFound;
    @Override
    public void retrieveValue(Player player, RetrievedValueExecutor<Location> onFound) {
        player.getServer().getPluginManager().registerEvents(this, GUILayer.moduleInitializer);
        this.player = player;
        this.onFound = onFound;
        player.closeInventory();
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        if(player != null && event.getPlayer().getUniqueId() == player.getUniqueId()) {
            onFound.onValueRetrieved(player.getLocation());
            event.setCancelled(true);
            HandlerList.unregisterAll(this);
        }
    }
}