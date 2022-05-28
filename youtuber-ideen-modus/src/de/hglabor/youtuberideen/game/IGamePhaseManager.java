package de.hglabor.youtuberideen.game;

public abstract class IGamePhaseManager {
    public AbstractGamePhase phase = null;
    public abstract void resetTimer();
}
