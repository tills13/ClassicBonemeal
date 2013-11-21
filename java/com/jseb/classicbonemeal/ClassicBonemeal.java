package com.jseb.classicbonemeal;

import com.jseb.classicbonemeal.listeners.EventListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ClassicBonemeal extends JavaPlugin {
	public static double mega_tree_prob;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		saveResource("config.yml", false);

		// read config values
		reloadConfig();
		mega_tree_prob = getConfig().getDouble("chance.mega_tree_regular", 0.75);
		saveConfig();
	}
	
	public void onDisable() {
	
	}
}
