package fr.theskyblockman.skylayer.player_identity;

import fr.theskyblockman.skylayer.LayeredPlugin;
import fr.theskyblockman.skylayer.Module;
import fr.theskyblockman.skylayer.scoreboard.ScoreboardManagement;

public class PlayerIdentityLayer extends Module {
    private static boolean isModuleInitialised = false;
    public static boolean isModuleInitialised() {
        return isModuleInitialised;
    }
    public static LayeredPlugin moduleInitializer;
    public static PlayerIdentityManager manager;
    private final PlayerIdentityManager curManager;


    public PlayerIdentityLayer(PlayerIdentityManager manager) {
        curManager = manager;
    }

    @Override
    public void onInit(LayeredPlugin plugin) {
        isModuleInitialised = true;
        moduleInitializer = plugin;

        manager = curManager;

        plugin.getServer().getPluginManager().registerEvents(new PlayerIdentityOverrider(), plugin);
    }

    @Override
    public void onDisable() {
        isModuleInitialised = false;
    }
}
