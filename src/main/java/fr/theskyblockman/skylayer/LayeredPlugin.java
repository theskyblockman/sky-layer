package fr.theskyblockman.skylayer;

import fr.theskyblockman.skylayer.layer.Module;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class LayeredPlugin extends JavaPlugin {
    private List<Module> enabledModules = new ArrayList<>();

    public void initModules(Module[] modules) {
        for(Module module : modules) {
            try {
                module.onInit(this);
                enabledModules.add(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(enabledModules.size() != modules.length) {
            Logger.getLogger(LayeredPlugin.class.getSimpleName()).warning(enabledModules.size() + " of " + modules.length + "plugins were enabled. Please check the thrown errors to see the reason of " + (modules.length - enabledModules.size() > 1 ? "those" : "this") + " failure" + (modules.length - enabledModules.size() > 1 ? "s" : ""));
        }
    }

    public void disableModules() {
        for(Module enabledModule : enabledModules) {
            try {
                enabledModule.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        disableModules();
    }
}