package fr.theskyblockman.skylayer.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GUIDescriptor implements Listener {
    public Inventory inv;
    public final Player currentPlayer;
    public boolean destroyOnClose = true;
    public ArrayList<GUIItem> currentItems = new ArrayList<>();
    private GUIState currentState;
    public GUIDescriptor(Player player, GUIState initialState, String GUIName) {
        currentState = initialState;
        this.inv = Bukkit.createInventory(null, currentState.inventorySize, GUIName);
        this.currentPlayer = player;
        this.openInventory(currentState);
        Bukkit.getServer().getPluginManager().registerEvents(this, GUILayer.moduleInitializer);
    }
    public void openInventory(@Nullable GUIState newState) {
        if(newState != null) {
            currentState = newState;
        }

        openInventory(null, currentState.inventorySize);
    }
    public void openInventory(@Nullable GUIState newState, int inventorySize) {
        if(newState != null) {
            currentState = newState;
        }
        this.inv = Bukkit.createInventory(null, inventorySize, currentState.guiName);
        currentItems = new ArrayList<>();
        for (GUIItem item :
                currentState.items) {
            if(item != null) {
                this.inv.setItem(item.slot, item.item);
                currentItems.add(item);
            }
        }
        if(currentState.filler != null) {
            for (GUIItem item : currentState.filler.getItems(this)) {
                if(item != null) {
                    this.inv.setItem(item.slot, item.item);
                    currentItems.add(item);
                }
            }
        }
        this.currentPlayer.openInventory(this.inv);
    }
    @EventHandler
    public void getOnPressed(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || event.getWhoClicked().getUniqueId() != this.currentPlayer.getUniqueId() || event.getInventory().getViewers() != this.inv.getViewers()) return;
        GUIItem clickedItem = currentState.getFromSlot(event.getSlot(), this);
        if(clickedItem != null) {
            if(clickedItem.runner != null) {
                clickedItem.runner.onPressed((Player) event.getWhoClicked(), this);
            } else if(clickedItem.eventRunner != null) {
                clickedItem.eventRunner.onPressed((Player) event.getWhoClicked(), this, event.getClick());
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        GUIDescriptor currentGUI = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(event.getPlayer().getUniqueId() != currentPlayer.getUniqueId() || !destroyOnClose || currentPlayer.getOpenInventory().getTopInventory().equals(inv)) return;
                HandlerList.unregisterAll(currentGUI);
                inv = null;
            }
        }.runTaskLaterAsynchronously(GUILayer.moduleInitializer, 1L);
    }

}
