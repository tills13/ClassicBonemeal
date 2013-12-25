package com.jseb.classicbonemeal;

import com.jseb.classicbonemeal.listeners.EventListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ClassicBonemeal extends JavaPlugin {
	public static double mega_tree_prob;
	public static boolean uses_permissions;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		saveResource("config.yml", false);

		// read config values
		reloadConfig();
		mega_tree_prob = getConfig().getDouble("chance.mega_tree_regular", 0.75);
		uses_permissions = getConfig().getBoolean("uses_permissions", false);

		getConfig().set("chance.mega_tree_regular", mega_tree_prob);
		getConfig().set("uses_permissions", uses_permissions);
		saveConfig();
	}
	
	public void onDisable() {
	
	}
}
