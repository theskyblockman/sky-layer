package fr.theskyblockman.skylayer.gui;

import java.util.List;

public interface InventoryFiller {
    List<GUIItem> getItems(GUIDescriptor gui);
}
