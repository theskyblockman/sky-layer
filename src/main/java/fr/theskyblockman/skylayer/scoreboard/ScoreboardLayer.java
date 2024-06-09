package fr.theskyblockman.skylayer.scoreboard;

import fr.theskyblockman.skylayer.LayeredPlugin;
import fr.theskyblockman.skylayer.layer.Module;
import org.bukkit.Bukkit;

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

        // Checks if the server is running a version of Minecraft
        // that requires team names to be shorter than 16 characters
        try {
            String minecraftVersion = Bukkit.getVersion();
            minecraftVersion = minecraftVersion.substring(minecraftVersion.indexOf("(MC: ") + 5, minecraftVersion.indexOf(")"));

            int majorMinecraftVersion = Integer.parseInt(minecraftVersion.split("\\.")[1]);

            ScoreboardManagement.usePlayerNamesAsIds = majorMinecraftVersion < 18;
        } catch(Exception e) {
            // Fallback to true, it's probably safe (maybe some NMS stuff wouldn't work using this system?)
            ScoreboardManagement.usePlayerNamesAsIds = true;
        }

        plugin.getServer().getPluginManager().registerEvents(new ScoreboardManagement(), plugin);
    }

    @Override
    public void onDisable() {
        isModuleInitialised = false;
    }
}
