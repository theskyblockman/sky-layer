package fr.theskyblockman.skylayer.gui;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIItem {
    public ItemStack item;
    public int slot;
    public ItemRunner runner;
    public EventItemRunner eventRunner;
    public GUIItem(Material itemType, int slot) {
        item = new ItemStack(itemType, 1);
        this.slot = slot;
    }
    public GUIItem(ItemStack stack, int slot) {
        item = stack;
        this.slot = slot;
    }
    public GUIItem setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        item.setItemMeta(meta);

        return this;
    }
    public GUIItem setLore(String[] lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return this;
    }

    public GUIItem setColor(DyeColor color) {
        //noinspection deprecation
        item.setDurability(color.getDyeData());
        return this;
    }
    public GUIItem hideFlags() {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return this;
    }
    public GUIItem setRunner(ItemRunner runner) {
        this.runner = runner;
        return this;
    }
    public GUIItem setRunner(EventItemRunner runner) {
        this.eventRunner = runner;
        return this;
    }

    public GUIItem setSlot(int newSlot) {
        this.slot = newSlot;
        return this;
    }
}
