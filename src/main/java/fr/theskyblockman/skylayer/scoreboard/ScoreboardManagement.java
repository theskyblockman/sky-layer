package fr.theskyblockman.skylayer.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreboardManagement implements Listener {
    public ScoreboardManagement() {}
    public static boolean useGlobalScoreboard;
    private static Objective currentGlobalObjective;
    public static ScoreboardComponent currentComponent;

    public static void makeScoreboardLocal(ScoreboardComponent component) {
        currentComponent = component;
        makeScoreboardLocal();
    }

    public static void makeScoreboardLocal() {
        useGlobalScoreboard = false;

        for(Player player : Bukkit.getOnlinePlayers()) {
            Objective currentObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

            if(currentObjective != null && Objects.equals(currentObjective.getDisplayName(), currentComponent.displayName)
                    && Objects.equals(currentObjective.getName(), player.getDisplayName())) {
                updateObjectiveFromComponent(currentObjective, currentComponent, player);
            } else {
                registerScoreboardForPlayer(player);
            }
        }
    }

    public static void makeScoreboardGlobal(ScoreboardComponent component) {
        currentComponent = component;
        makeScoreboardGlobal();
    }

    public static void makeScoreboardGlobal() {
        useGlobalScoreboard = true;

        if(currentGlobalObjective != null) {
            currentGlobalObjective.setDisplayName(currentComponent.displayName);
            return;
        }

        Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(currentComponent.displayName);
        currentGlobalObjective = objective;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(objective.getScoreboard());
            updateScoreboard(player);
        }
    }

    public static void registerScoreboardForPlayer(Player player) {
        Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective newObjective = newScoreboard.registerNewObjective(player.getDisplayName(), "dummy");
        newObjective.setDisplayName(currentComponent.displayName);
        newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(newScoreboard);
        updateScoreboard(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(useGlobalScoreboard) {
            updateScoreboard();
        } else {
            registerScoreboardForPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(useGlobalScoreboard) {
            updateScoreboard();
        } else {
            event.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
        }
    }

    public static void updateScoreboard(Player player) {
        if(useGlobalScoreboard) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateObjectiveFromComponent(currentGlobalObjective, currentComponent);
                }
            }.runTask(ScoreboardLayer.moduleInitializer);
        } else {
            Objective currentObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

            if(Objects.equals(currentObjective.getDisplayName(), currentComponent.displayName)
                    && Objects.equals(currentObjective.getName(), player.getDisplayName())) {
                updateObjectiveFromComponent(currentObjective, currentComponent, player);
            }
        }
    }

    public static void updateScoreboard() {
        if(useGlobalScoreboard) {
            updateObjectiveFromComponent(currentGlobalObjective, currentComponent);
        } else {
            for(Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
        }
    }

    private static void updateObjectiveFromComponent(Objective objective, ScoreboardComponent component, Player player) {
        for(String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }

        String[] values = component.updater.updateScoreboardComponents(player);
        ArrayList<String> shrunkValues = new ArrayList<>();

        for(String value : values) {
            if(value == null) continue;

            if(value.length() <= 40) {
                shrunkValues.add(value);
            } else {
                StringBuilder sb = new StringBuilder();
                for(String valuePart : value.split(" ")) {
                    if(sb.length() + valuePart.length() > 39) {
                        shrunkValues.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    sb.append(valuePart).append(" ");
                }
                shrunkValues.add(sb.toString());
            }
        }
        int currentValue = shrunkValues.size();
        for(String value : shrunkValues) {
            objective.getScore(value).setScore(currentValue);
            currentValue--;
        }
    }

    private static void updateObjectiveFromComponent(Objective objective, ScoreboardComponent component) {
        if(useGlobalScoreboard) {
            updateObjectiveFromComponent(objective, component, null);
        } else {
            for(Player player : Bukkit.getOnlinePlayers()) {
                updateObjectiveFromComponent(objective, component, player);
            }
        }
    }
}
