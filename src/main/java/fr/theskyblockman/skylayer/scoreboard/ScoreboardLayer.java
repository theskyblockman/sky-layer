package fr.theskyblockman.skylayer.scoreboard;

import fr.theskyblockman.skylayer.LayeredPlugin;
import fr.theskyblockman.skylayer.layer.Module;

public class ScoreboardLayer extends Module {
    private static boolean isModuleInitialised = false;
    public static boolean isModuleInitialised() {
        return isModuleInitialised;
    }
    public static LayeredPlugin moduleInitializer;
    @Override
    public void onInit(LayeredPlugin plugin) {
        isModuleInitialised = true;
        moduleInitializer = plugin;

        plugin.getServer().getPluginManager().registerEvents(new ScoreboardManagement(), plugin);
    }

    @Override
    public void onDisable() {
        isModuleInitialised = false;
    }
}
