package net.botwithus.Runecrafting;

import java.util.Date;

public class PlayerInfo {
    private String name;
    private long time;
    private int world;

    public PlayerInfo(String name, long time, int world) {
        this.name = name;
        this.time = time;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public int getWorld() {
        return world;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Time: " + new Date(time) + ", World: " + world;
    }
}
