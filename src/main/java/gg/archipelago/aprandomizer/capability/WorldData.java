package gg.archipelago.aprandomizer.capability;

public class WorldData {

    private String seedName = "";

    private int dragonState = DRAGON_ASLEEP;

    private boolean jailPlayers = true;

    public static final int DRAGON_KILLED = 30;
    public static final int DRAGON_SPAWNED = 20;
    public static final int DRAGON_WAITING = 15;
    public static final int DRAGON_ASLEEP = 10;

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }

    public String getSeedName() {
        return seedName;
    }

    public void setDragonState(int dragonState) {
        this.dragonState = dragonState;
    }

    public int getDragonState() {
        return dragonState;
    }

    public boolean getJailPlayers() {
        return jailPlayers;
    }

    public void setJailPlayers(boolean jailPlayers) {
        this.jailPlayers = jailPlayers;
    }

}
