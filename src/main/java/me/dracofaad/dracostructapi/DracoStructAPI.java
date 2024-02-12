package me.dracofaad.dracostructapi;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class DracoStructAPI extends JavaPlugin {
    public static DracoStructAPI instance;

    public static String PluginFolder;
    public static String StructuresFolder;


    @Override
    public void onEnable() {
        instance = this;

        PluginFolder = getDataFolder().getAbsolutePath();
        StructuresFolder = PluginFolder + "/Structures";

        if (!(new File(StructuresFolder).exists())) {
            new File(StructuresFolder).mkdirs();
        }

        // Plugin startup logic
        StructAPICom structAPICom = new StructAPICom();
        getCommand("structureapi").setExecutor(structAPICom);
        getCommand("structureapi").setTabCompleter(structAPICom);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DracoStructAPI getInstance() {
        return instance;
    }
}
