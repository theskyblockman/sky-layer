package fr.theskyblockman.skylayer.shared_team;

import fr.theskyblockman.skylayer.LayeredPlugin;
import fr.theskyblockman.skylayer.Module;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("unused")
public class SharedTeamLayer extends Module {
    private static boolean isModuleInitialised = false;
    public static boolean isModuleInitialised() {
        return isModuleInitialised;
    }
    public static LayeredPlugin moduleInitializer;

    public static SharedTeamManager manager;

    public SharedTeamLayer(SharedTeamManager manager) {
        SharedTeamLayer.manager = manager;
    }

    /**
     * Create a new SharedTeamLayer with a local SharedTeamManager which is reset at each reload.
     *
     * @return the new layer.
     */
    public static SharedTeamLayer local() {
        return new SharedTeamLayer(new SharedTeamManager.LocalSharedTeamManager(null));
    }

    /**
     * Create a new SharedTeamLayer with a local SharedTeamManager which is persisted in a local file.
     *
     * @param plugin the plugin to initialize the module with.
     * @param fileName the name of the file to save the teams in. Defaults to {@code "shared_teams.bin"}.
     * @return the new layer.
     */
    public static SharedTeamLayer persistent(LayeredPlugin plugin, @Nullable String fileName) {
        if(fileName == null) {
            fileName = "shared_teams.bin";
        }
        return new SharedTeamLayer(
                new SharedTeamManager.LocalSharedTeamManager(
                        new File(plugin.getDataFolder(), fileName).toPath()
                )
        );
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
