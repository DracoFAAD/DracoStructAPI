package me.dracofaad.dracostructapi;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serial;
import java.io.Serializable;

public class PlacedStructure implements Serializable {
    @Serial
    private static final long serialVersionUID = 1048484L;

    private double Corner1X = 0.0;
    private double Corner1Y = 0.0;
    private double Corner1Z = 0.0;
    private double Corner2X = 0.0;
    private double Corner2Y = 0.0;
    private double Corner2Z = 0.0;
    private final Structure structure;

    public PlacedStructure(double corner1X, double corner1Y, double corner1Z, double corner2X, double corner2Y, double corner2Z, Structure structure) {
        Corner1X = corner1X;
        Corner1Y = corner1Y;
        Corner1Z = corner1Z;
        Corner2X = corner2X;
        Corner2Y = corner2Y;
        Corner2Z = corner2Z;
        this.structure = structure;
    }

    public double getCorner1X() {
        return Corner1X;
    }

    public double getCorner1Y() {
        return Corner1Y;
    }

    public double getCorner1Z() {
        return Corner1Z;
    }

    public double getCorner2X() {
        return Corner2X;
    }

    public double getCorner2Y() {
        return Corner2Y;
    }

    public double getCorner2Z() {
        return Corner2Z;
    }

    public Structure getStructure() {
        return structure;
    }

    public boolean IsIn(Location location) {
        int X = location.getBlockX();
        int Y = location.getBlockX();
        int Z = location.getBlockX();

        double Corner1X = Math.min(this.Corner1X, this.Corner2X);
        double Corner1Y = Math.min(this.Corner1Y, this.Corner2Y);
        double Corner1Z = Math.min(this.Corner1Z, this.Corner2Z);
        double Corner2X = Math.max(this.Corner1X, this.Corner2X);
        double Corner2Y = Math.max(this.Corner1Y, this.Corner2Y);
        double Corner2Z = Math.max(this.Corner1Z, this.Corner2Z);

        if (X < Corner1X || X > Corner2X) return false;
        if (Y < Corner1Y || Y > Corner2Y) return false;
        if (Z < Corner1Z || Z > Corner2Z) return false;

        return true;
    }

    public Location randomLocation() {
        double Corner1X = Math.min(this.Corner1X, this.Corner2X);
        double Corner1Y = Math.min(this.Corner1Y, this.Corner2Y);
        double Corner1Z = Math.min(this.Corner1Z, this.Corner2Z);
        double Corner2X = Math.max(this.Corner1X, this.Corner2X);
        double Corner2Y = Math.max(this.Corner1Y, this.Corner2Y);
        double Corner2Z = Math.max(this.Corner1Z, this.Corner2Z);

        int X = getRandomNumber((int) Corner1X, (int) Corner2X);
        int Y = getRandomNumber((int) Corner1Y, (int) Corner2Y);
        int Z = getRandomNumber((int) Corner1Z, (int) Corner2Z);

        return new Location(null, X, Y, Z);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max + 1) - min)) + min);
    }
}
