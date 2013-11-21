package com.jseb.classicbonemeal.listeners;

import com.jseb.classicbonemeal.Utils;
import com.jseb.classicbonemeal.ClassicBonemeal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.Material;
import org.bukkit.material.Dispenser;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;


public class EventListener implements Listener {
	public ClassicBonemeal plugin;

	public EventListener(ClassicBonemeal plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null || e.getItem() == null) return;

		Block clickedBlock = e.getClickedBlock();
		boolean used = false; 
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getItem().getData().getData() == 15) {
				if (clickedBlock.getType().equals(Material.CROPS) 
				 || clickedBlock.getType().equals(Material.POTATO) 
				 || clickedBlock.getType().equals(Material.CARROT) 
				 || clickedBlock.getType().equals(Material.NETHER_WARTS)) used = Utils.growCrop(clickedBlock);
				else if (clickedBlock.getType().equals(Material.SAPLING)) used = Utils.growTree(clickedBlock);
				else if (clickedBlock.getType().equals(Material.PUMPKIN_STEM) 
					 || (clickedBlock.getType().equals(Material.MELON_STEM)) 
					 || (clickedBlock.getType().equals(Material.COCOA))) used = Utils.growFromStem(clickedBlock);	
				e.setCancelled(true);
			}
		}

		if (used) {
			if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
				if (e.getItem().getAmount() == 1) e.getPlayer().setItemInHand(null);
				else e.getItem().setAmount(e.getItem().getAmount() - 1);
			}
		}
	}

	@EventHandler
	public void onBlockDispenseEvent(BlockDispenseEvent e) {
		if (e.getBlock() == null || e.getItem() == null) return;
		
		Block relative = e.getBlock().getRelative(((Dispenser) e.getBlock().getState().getData()).getFacing());

		if (e.getItem().getData().getData() == 15) {
			if (relative.getType().equals(Material.CROPS) 
			 || relative.getType().equals(Material.POTATO) 
			 || relative.getType().equals(Material.CARROT) 
			 || relative.getType().equals(Material.NETHER_WARTS)) Utils.growCrop(relative);
			else if (relative.getType().equals(Material.SAPLING)) Utils.growTree(relative);
			else if (relative.getType().equals(Material.PUMPKIN_STEM) 
				 || (relative.getType().equals(Material.MELON_STEM)) 
				 || (relative.getType().equals(Material.COCOA))) Utils.growFromStem(relative);
		}
	}
}