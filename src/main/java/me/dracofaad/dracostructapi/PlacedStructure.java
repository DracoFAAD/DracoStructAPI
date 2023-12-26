package me.dracofaad.dracostructapi;

import org.bukkit.World;

public class PlacedStructure {
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
}
