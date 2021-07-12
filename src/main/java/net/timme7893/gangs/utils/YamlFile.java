package net.timme7893.gangs.utils;

import java.io.File;

import net.timme7893.gangs.GangsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;

public class YamlFile {

    private FileConfiguration fileconf;
    @Getter
    private File file;
    private String name;

    public YamlFile(String name) {
        this.name = name;
        init();
    }

    private void init() {
        new File(GangsPlugin.getInstance().getDataFolder() + File.separator + "inventories").mkdirs();
        file = new File(GangsPlugin.getInstance().getDataFolder() + File.separator + "inventories", name + ".yml");
        fileconf = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getData() {
        return fileconf;
    }

    public void reloadData() {
        fileconf = YamlConfiguration.loadConfiguration(file);
    }
    
    public void delete() {
        file.delete();
        file = null;
        fileconf = null;
    }

    public void saveData() {
        try {
            if (fileconf != null && file != null) {
                fileconf.save(file);
            } else {
                init();
                fileconf.save(file);
            }
        } catch (Exception e) {
            GangsPlugin.getInstance().getLogger()
                    .info(ChatColor.RED + "An error occured whilst saving " + name + ".yml!");
            e.printStackTrace();
        }
    }
}