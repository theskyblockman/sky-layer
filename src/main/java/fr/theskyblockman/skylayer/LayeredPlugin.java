package fr.theskyblockman.skylayer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class LayeredPlugin extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Layers management");

    private final List<Module> enabledModules = new ArrayList<>();

    @SuppressWarnings("unused")
    public void initModules(Module[] modules) {
        for(Module module : modules) {
            try {
                module.onInit(this);
                enabledModules.add(module);
            } catch (Exception e) {
                LogRecord lr = new LogRecord(Level.WARNING, "An error occurred while initializing a module: " + e.getMessage());
                lr.setSourceClassName("LayeredPlugin");
                lr.setSourceMethodName("initModules");
                lr.setThrown(e);
                logger.log(lr);
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
                LogRecord lr = new LogRecord(Level.WARNING, "An error occurred while disabling a module: " + e.getMessage());
                lr.setSourceClassName("LayeredPlugin");
                lr.setSourceMethodName("disableModules");
                lr.setThrown(e);
                logger.log(lr);
            }
        }
        enabledModules.clear();
    }

    @Override
    public void onDisable() {
        disableModules();
    }
}