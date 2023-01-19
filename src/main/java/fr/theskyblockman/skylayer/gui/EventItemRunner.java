package fr.theskyblockman.skylayer.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface EventItemRunner {
    void onPressed(Player clicker, GUIDescriptor gui, ClickType clickType);
}
