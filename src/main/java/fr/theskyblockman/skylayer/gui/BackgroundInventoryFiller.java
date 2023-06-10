package fr.theskyblockman.skylayer.gui;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class BackgroundInventoryFiller implements InventoryFiller{
    @Override
    public List<GUIItem> getItems(GUIDescriptor gui) {
        List<Integer> filledSlots = new ArrayList<>();
        for(GUIItem item : gui.currentItems) {
            filledSlots.add(item.slot);
        }
        List<GUIItem> fillerSlots = new ArrayList<>();

        for (int index = 0; index < gui.inv.getSize(); index++) {
            if(!filledSlots.contains(index)) {
                fillerSlots.add(fillerItem.clone().setSlot(index));
            }
        }
        return fillerSlots;
    }

    public final GUIItem fillerItem;

    public BackgroundInventoryFiller(GUIItem fillerItem) {
        this.fillerItem = fillerItem;
    }

    public BackgroundInventoryFiller(DyeColor color) {
        this.fillerItem = new GUIItem(Material.matchMaterial(color.name() + "_STAINED_GLASS_PANE"), 1).setColor(color).setDisplayName("§r").hideFlags();
    }
}
