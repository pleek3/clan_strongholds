package de.teamhardcore.strongholds.bukkit.game.phases;

public interface Phase {

    String HOLOGRAM_PHASE_ID = "PHASE_HOLOGRAM";

    void onStart();

    void onEnd();

    String getName();

}
