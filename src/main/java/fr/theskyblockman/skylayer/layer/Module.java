package fr.theskyblockman.skylayer.layer;

import fr.theskyblockman.skylayer.LayeredPlugin;

public abstract class Module {
    public abstract void onInit(LayeredPlugin plugin);
    public abstract void onDisable();
}