package me.dracofaad.dracostructapi;

public class StructureSize {
    private final int SizeX;
    private final int SizeY;
    private final int SizeZ;
    private final double HighestX;
    private final double HighestY;
    private final double HighestZ;
    private final double LowestX;
    private final double LowestY;
    private final double LowestZ;

    public StructureSize(int SizeX, int SizeY, int SizeZ, double HighestX, double HighestY, double HighestZ, double LowestX, double LowestY, double LowestZ) {
        this.SizeX = SizeX;
        this.SizeY = SizeY;
        this.SizeZ = SizeZ;

        this.HighestX = HighestX;
        this.HighestY = HighestY;
        this.HighestZ = HighestZ;

        this.LowestX = LowestX;
        this.LowestY = LowestY;
        this.LowestZ = LowestZ;
    }

    public int getSizeX() {
        return SizeX;
    }

    public int getSizeY() {
        return SizeY;
    }

    public int getSizeZ() {
        return SizeZ;
    }

    public double getHighestX() {
        return HighestX;
    }

    public double getHighestY() {
        return HighestY;
    }

    public double getHighestZ() {
        return HighestZ;
    }

    public double getLowestX() {
        return LowestX;
    }

    public double getLowestY() {
        return LowestY;
    }

    public double getLowestZ() {
        return LowestZ;
    }
}
