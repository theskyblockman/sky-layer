package fr.theskyblockman.skylayer.gui;

import fr.theskyblockman.skylayer.LayeredPlugin;
import fr.theskyblockman.skylayer.layer.Module;

public class GUILayer extends Module {
    private static boolean isModuleInitialised = false;
    public static LayeredPlugin moduleInitializer;
    public static boolean isModuleInitialised() {
        return isModuleInitialised;
    }
    @Override
    public void onInit(LayeredPlugin plugin) {
        isModuleInitialised = true;
        moduleInitializer = plugin;
    }

    @Override
    public void onDisable() {
        isModuleInitialised = false;
    }
}
