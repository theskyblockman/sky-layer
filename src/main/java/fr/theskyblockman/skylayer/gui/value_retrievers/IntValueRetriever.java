package fr.theskyblockman.skylayer.gui.value_retrievers;

import fr.theskyblockman.skylayer.gui.GUILayer;
import fr.theskyblockman.skylayer.gui.RetrievedValueExecutor;
import fr.theskyblockman.skylayer.gui.ValueSetter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class IntValueRetriever implements Listener, ValueSetter<Integer> {
    @Override
    public void retrieveValue(Player player, RetrievedValueExecutor<Integer> onFound) {
        player.getServer().getPluginManager().registerEvents(this, GUILayer.moduleInitializer);
        player.closeInventory();
        this.currentPlayer = player;
        this.onFound = onFound;
    }
    private Player currentPlayer;
    private RetrievedValueExecutor<Integer> onFound;
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if(currentPlayer != null && event.getPlayer().getUniqueId() == currentPlayer.getUniqueId()) {
            try {
                onFound.onValueRetrieved(Integer.parseInt(event.getMessage()));
                HandlerList.unregisterAll(this);
            } catch (NumberFormatException error) {
                currentPlayer.sendMessage(ChatColor.RED + "Veuillez rentrer un nombre valide.");
            }
            event.setCancelled(true);
        }
    }
}
