package fr.theskyblockman.skylayer.gui;

import org.bukkit.entity.Player;

public interface ValueSetter<T> {
    void retrieveValue(Player player, RetrievedValueExecutor<T> onFound);
}
