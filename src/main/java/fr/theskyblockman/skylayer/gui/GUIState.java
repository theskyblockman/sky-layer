package fr.theskyblockman.skylayer.gui;

import java.util.List;

public class GUIState {
    public final List<GUIItem> items;
    public final String guiName;
    public final int inventorySize;
    public final List<InventoryFiller> fillers;

    public GUIState(List<GUIItem> items, String guiName, int inventorySize) {
        this.items = items;
        this.guiName = guiName;
        this.inventorySize = inventorySize;
        this.fillers = null;
    }
    public GUIState(List<GUIItem> items, String guiName, int inventorySize, List<InventoryFiller> fillers) {
        this.items = items;
        this.guiName = guiName;
        this.inventorySize = inventorySize;
        this.fillers = fillers;
    }

    GUIItem getFromSlot(int slot, GUIDescriptor gui) {
        for (GUIItem state : gui.currentItems) {
            if(state.slot == slot) return state;
        }
        return null;
    }
}
