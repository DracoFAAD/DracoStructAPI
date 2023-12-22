package me.dracofaad.dracostructapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Structure {
    public List<StructureBlock> blocks = new ArrayList<>();
    public String StructureName = "";
    public String StructureString = "";

    /**
     *
     * This function returns a brand new Structure which is retrieved from the real world.
     *
     * @param name Name of the Structure
     * @param world The world where the Structure should be saved
     * @param x1 X coordination of corner 1
     * @param y1 Y coordination of corner 1
     * @param z1 Z coordination of corner 1
     * @param x2 X coordination of corner 2
     * @param y2 Y coordination of corner 2
     * @param z2 Z coordination of corner 2
     */
    public Structure(String name, World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.StructureName = name;

        //Corner Correction
        if (x1 > x2) {
            double temp = x2;
            x2 = x1;
            x1 = temp;
        }

        if (y2 > y1) {
            double temp = y1;
            y1 = y2;
            y2 = temp;
        }

        if (z1 > z2) {
            double temp = z2;
            z2 = z1;
            z1 = temp;
        }


        for (int x = (int) x1; x <= (int) x2; x++) {
            for (int z = (int) z1; z <= (int) z2; z++) {
                for (int y = (int) y1; y >= (int) y2; y--) {
                    double xOffset = x-x1;
                    double yOffset = y-y2;
                    double zOffset = z-z1;

                    Location location = new Location(world, x, y, z);
                    BlockData blockData = location.getBlock().getBlockData();
                    String blockDataString = blockData.getAsString();
                    blocks.add(new StructureBlock(blockDataString, xOffset, yOffset, zOffset));
                }
            }
        }

        reloadStructureString();
    }

    public Structure(String StructureString) {
        this.StructureString = StructureString;
        parseStructureString();
    }

    public Structure(File StructureFile) {
        try {
            this.StructureString = String.join("\n", Files.readAllLines(StructureFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        parseStructureString();
    }

    public Structure(InputStream StructureInputStream) {
        this.StructureString = inputStreamToString(StructureInputStream);
        parseStructureString();
    }

    public static String inputStreamToString(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveAsFile() {
        String path = DracoStructAPI.StructuresFolder + "/" + StructureName + ".dStructure";
        try {
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());
            Files.write(pathToFile, StructureString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAsFile(String path) {
        try {
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());
            Files.write(pathToFile, StructureString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeAtLocation(Location location) {
        for (StructureBlock block : blocks) {
            if (block.blockData.split("\\[", 2).length == 2) {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData("[" + block.blockData.split("\\[", 2)[1]);
                location.clone().add(block.xOffset, block.yOffset, block.zOffset).getBlock().setBlockData(blockData);
            } else {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData();
                location.clone().add(block.xOffset, block.yOffset, block.zOffset).getBlock().setBlockData(blockData);
            }

        }
    }

    private void parseStructureString() {
        blocks.clear();
        String[] StructureBlocksLines = StructureString.split("\n");
        for (String block : StructureBlocksLines) {
            String[] coordinates = block.split("@")[0].split(";");
            String blockData = block.split("@")[1];
            double xOffset = Double.parseDouble(coordinates[0]);
            double yOffset = Double.parseDouble(coordinates[1]);
            double zOffset = Double.parseDouble(coordinates[2]);
            blocks.add(new StructureBlock(blockData, xOffset, yOffset, zOffset));
        }
    }

    private void reloadStructureString() {
        StringBuilder newStructureString = new StringBuilder();
        for (StructureBlock structureBlock : blocks) {
            if (newStructureString.toString() == "") {
                newStructureString.append(structureBlock.xOffset).append(";").append(structureBlock.yOffset).append(";").append(structureBlock.zOffset).append("@").append(structureBlock.blockData);
            } else {
                newStructureString.append("\n").append(structureBlock.xOffset).append(";").append(structureBlock.yOffset).append(";").append(structureBlock.zOffset).append("@").append(structureBlock.blockData);
            }
        }
        StructureString = newStructureString.toString();
    }

    public static class StructureBlock {
        public double xOffset = 0;
        public double yOffset = 0;
        public double zOffset = 0;
        public String blockData;

        public StructureBlock(String blockData, double xOffset, double yOffset, double zOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.blockData = blockData;
        }
    }
}
