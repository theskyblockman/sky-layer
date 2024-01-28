package fr.theskyblockman.skylayer.scoreboard;

public class ScoreboardComponent {
    public ScoreboardComponentUpdater updater;
    public String displayName;
    public ScoreboardComponent(ScoreboardComponentUpdater updater, String displayName) {
        this.updater = updater;
        this.displayName = displayName;
    }
}
