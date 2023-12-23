package me.dracofaad.dracostructapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StructAPICom implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdName, @NotNull String[] args) {
        if (!sender.hasPermission("structureapi.*") && !sender.isOp()) return false;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.WHITE + "Available Sub-Commands:" + ChatColor.GOLD + "\n- saveStructure\n- placeStructure");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equals("saveStructure")) {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + cmdName + " saveStructure StructureName x1 y1 z1 x2 y2 z2");
                return true;
            }
            else if (args[0].equals("placeStructure")) {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + cmdName + " placeStructure StructureName");
                return true;
            }
        }

        if (args[0].equals("saveStructure")) {
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED + "You can only do this as a Player!");
                return true;
            }

            if (args.length != 8) {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + cmdName + " saveStructure StructureName x1 y1 z1 x2 y2 z2");
                return true;
            }

            if (!isNumeric(args[2]) || !isNumeric(args[3]) || !isNumeric(args[4]) || !isNumeric(args[5]) || !isNumeric(args[6]) || !isNumeric(args[7])) {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + cmdName + " saveStructure StructureName x1 y1 z1 x2 y2 z2");
                return true;
            }

            if (!validateStringFilenameUsingRegex(args[1])) {
                sender.sendMessage(ChatColor.RED + "The structure name is invalid!");
                return true;
            }

            String StructureName = args[1];
            double x1 = Double.parseDouble(args[2]);
            double y1 = Double.parseDouble(args[3]);
            double z1 = Double.parseDouble(args[4]);
            double x2 = Double.parseDouble(args[5]);
            double y2 = Double.parseDouble(args[6]);
            double z2 = Double.parseDouble(args[7]);

            Structure structure = new Structure(StructureName, ((Player) sender).getWorld(), x1, y1, z1, x2, y2, z2);
            structure.saveAsFile();

            sender.sendMessage("Sucessfully saved structure " + ChatColor.BOLD + structure.StructureName + ChatColor.RESET + "!");
            return true;
        }

        if (args[0].equals("placeStructure")) {
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED + "You can only do this as a Player!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + cmdName + " placeStructure StructureName");
                return true;
            }

            if (!validateStringFilenameUsingRegex(args[1])) {
                sender.sendMessage(ChatColor.RED + "The structure name is invalid!");
                return true;
            }

            if (!new File(DracoStructAPI.StructuresFolder + "/" + args[1] + ".dStructure").exists()) {
                sender.sendMessage(ChatColor.RED + "The structure doesnt exist!");
                return true;
            }

            File file = new File(DracoStructAPI.StructuresFolder + "/" + args[1] + ".dStructure");
            Structure structure = Structure.loadData(file);

            if (args.length > 2) {
                if (args[2].equals("Center")) {
                    structure.placeCenterXZatLocation(((Player) sender).getLocation());
                    return true;
                }
            }
            structure.placeAtLocation(((Player) sender).getLocation());
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> returnedArgs = new ArrayList<>();
        Player player = (Player) commandSender;
        Block targetBlock = getTargetBlock(player, 5);
        if (args.length == 1) {
            returnedArgs.add("saveStructure");
            returnedArgs.add("placeStructure");
        }
        else if (args.length == 2) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add("name");
            }
            else if (args[0].equals("placeStructure")) {
                File folder = new File(DracoStructAPI.StructuresFolder);
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    returnedArgs.add(listOfFiles[i].getName().replace(".dStructure", ""));
                }
                returnedArgs.add("{{StructureName}}");
            }
        }
        else if (args.length == 3) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                returnedArgs.add(targetBlock.getX() + " " + targetBlock.getY());
                returnedArgs.add(targetBlock.getX()+"");
            } else if (args[0].equals("placeStructure")) {
                returnedArgs.add("Center");
            }
        }
        else if (args.length == 4) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getY() + " " + targetBlock.getZ());
                returnedArgs.add(targetBlock.getY() + "");
            }
        }
        else if (args.length == 5) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getZ() + "");
            }
        }
        else if (args.length == 6) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ());
                returnedArgs.add(targetBlock.getX() + " " + targetBlock.getY());
                returnedArgs.add(targetBlock.getX() + "");
            }
        }
        else if (args.length == 7) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getY() + " " + targetBlock.getZ());
                returnedArgs.add(targetBlock.getY() + "");
            }
        }
        else if (args.length == 8) {
            if (args[0].equals("saveStructure")) {
                returnedArgs.add(targetBlock.getZ() + "");
            }
        }
        return returnedArgs;
    }

    public static boolean isPlayer(CommandSender commandSender) {
        return (commandSender instanceof Player);
    }

    public static boolean isConsole(CommandSender commandSender) {
        return (commandSender instanceof ConsoleCommandSender);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static final String REGEX_PATTERN = "^[A-Za-z0-9.\\-_]{1,255}$";

    public static boolean validateStringFilenameUsingRegex(String filename) {
        if (filename == null) {
            return false;
        }
        return filename.matches(REGEX_PATTERN);
    }
}
