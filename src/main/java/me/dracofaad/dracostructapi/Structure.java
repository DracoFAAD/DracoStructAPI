package me.dracofaad.dracostructapi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Structure implements Serializable {
    @Serial
    private static final long serialVersionUID = 1048483L;
    public HashSet<StructureBlock> blocks = new HashSet<>();
    public String StructureName = "";
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
                    Block block = location.getBlock();
                    BlockData blockData = block.getBlockData();
                    String blockDataString = blockData.getAsString();
                    HashSet<Tuple<Integer, Map<String, Object>>> containerData = null;

                    if (block.getState() instanceof Container) {
                        containerData = new HashSet<>();
                        if (block.getState() instanceof Chest) {
                            ContainerToHashSet(((Chest) block.getState()).getBlockInventory(), containerData);
                        } else {
                            ContainerToHashSet(((Container) block.getState()).getInventory(), containerData);
                        }
                    }

                    blocks.add(new StructureBlock(blockDataString, xOffset, yOffset, zOffset, containerData));
                }
            }
        }
    }

    private void ContainerToHashSet(Inventory inventory, HashSet<Tuple<Integer, Map<String, Object>>> hashSet) {
        for (ListIterator<ItemStack> iterator = inventory.iterator(); iterator.hasNext(); ) {
            Integer index = iterator.nextIndex();
            ItemStack itemStack = iterator.next();
            if (itemStack == null) continue;
            hashSet.add(new Tuple<>(index, itemStack.serialize()));
        }
    }

    private void HashSetToContainer(Inventory inventory, HashSet<Tuple<Integer, Map<String, Object>>> hashSet) {
        for (Tuple<Integer, Map<String, Object>> item : hashSet) {
            inventory.setItem(item.Value1, ItemStack.deserialize(item.Value2));
        }
    }

    //#region Public Saving
    public void saveAsFile() {
        String path = DracoStructAPI.StructuresFolder + "/" + StructureName + ".dStructure";
        saveData(path);
    }

    public void saveAsFile(String path) {
        saveData(path);
    }
    //#endregion Public Saving
    //#region Placing
    public void placeAtLocation(Location location) {
        for (StructureBlock block : blocks) {
            Block realBlock = location.clone().add(block.xOffset, block.yOffset, block.zOffset).getBlock();
            if (block.blockData.split("\\[", 2).length == 2) {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData("[" + block.blockData.split("\\[", 2)[1]);
                realBlock.setBlockData(blockData);
            } else {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData();
                realBlock.setBlockData(blockData);
            }

            if (block.containerData != null) {
                if (realBlock.getState() instanceof Container) {
                    if (realBlock.getState() instanceof Chest) {
                        HashSetToContainer(((Chest) realBlock.getState()).getBlockInventory(), block.containerData);
                    } else {
                        HashSetToContainer(((Container) realBlock.getState()).getInventory(), block.containerData);
                    }
                }
            }
        }
    }

    public void placeAtLocation(LimitedRegion limitedRegion, int x, int y, int z) {
        for (StructureBlock block : blocks) {
            int xWithOffset = x + (int) block.xOffset;
            int yWithOffset = y + (int) block.yOffset;
            int zWithOffset = z + (int) block.zOffset;

            if (block.blockData.split("\\[", 2).length == 2) {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData("[" + block.blockData.split("\\[", 2)[1]);
                limitedRegion.setBlockData(xWithOffset, yWithOffset, zWithOffset, blockData);
            } else {
                BlockData blockData = Material.matchMaterial(block.blockData.split("\\[")[0]).createBlockData();
                limitedRegion.setBlockData(xWithOffset, yWithOffset, zWithOffset, blockData);
            }


            if (block.containerData != null) {
                BlockState state = limitedRegion.getBlockState(xWithOffset, yWithOffset, zWithOffset);
                if (state instanceof Container) {
                    if (state instanceof Chest) {
                        HashSetToContainer(((Chest) state).getBlockInventory(), block.containerData);
                    } else {
                        HashSetToContainer(((Container) state).getInventory(), block.containerData);
                    }
                }
            }
        }
    }

    public void placeCenterXZatLocation(Location location) {
        double centerXOffset = 0;
        double centerZOffset = 0;

        double highestX = 0;
        double highestZ = 0;
        for (StructureBlock block : blocks) {
            if(block.xOffset > highestX) {
                highestX = block.xOffset;
            }

            if(block.zOffset > highestZ) {
                highestZ = block.zOffset;
            }
        }

        centerZOffset -= (highestZ / 2);
        centerXOffset -= (highestX / 2);

        //Place the Blocks
        placeAtLocation(location.clone().add(centerXOffset, 0, centerZOffset));
    }

    public void placeCenterXZatLocation(LimitedRegion limitedRegion, int x, int y, int z) {
        double centerXOffset = 0;
        double centerZOffset = 0;

        double highestX = 0;
        double highestZ = 0;
        for (StructureBlock block : blocks) {
            if(block.xOffset > highestX) {
                highestX = block.xOffset;
            }

            if(block.zOffset > highestZ) {
                highestZ = block.zOffset;
            }
        }

        centerZOffset -= (highestZ / 2);
        centerXOffset -= (highestX / 2);

        //Place the Blocks
        placeAtLocation(limitedRegion, (int) (x + centerXOffset), y, (int) (z + centerZOffset));
    }


    //#endregion Placing
    //#region Saving Data
    private boolean saveData(String FilePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(FilePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    //#endregion Saving Data
    //#region Loading Data

    public static Structure loadData(File file) {
        try {
            file.createNewFile();
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            Structure data = (Structure) in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Structure loadData(InputStream inputStream) {
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            BukkitObjectInputStream in = new BukkitObjectInputStream(gzipInputStream);
            Structure data = (Structure) in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Structure loadData(URL url) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(new File(url.toURI()))));
            Structure data = (Structure) in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    //#endregion Loading Data

    public static class StructureBlock implements Serializable {
        public double xOffset = 0;
        public double yOffset = 0;
        public double zOffset = 0;
        public String blockData;
        public HashSet<Tuple<Integer, Map<String, Object>>> containerData;

        public StructureBlock(String blockData, double xOffset, double yOffset, double zOffset, HashSet<Tuple<Integer, Map<String, Object>>> containerData) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.blockData = blockData;
            this.containerData = containerData;
        }
    }

    public static class Tuple<T1, T2> implements Serializable {
        public T1 Value1;
        public T2 Value2;

        public Tuple(T1 Value1, T2 Value2) {
            this.Value1 = Value1;
            this.Value2 = Value2;
        }
    }
}
