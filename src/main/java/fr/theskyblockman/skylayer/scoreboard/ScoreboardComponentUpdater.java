package fr.theskyblockman.skylayer.scoreboard;

import org.bukkit.entity.Player;

public interface ScoreboardComponentUpdater {
    String[] updateScoreboardComponents(Player player);
}
