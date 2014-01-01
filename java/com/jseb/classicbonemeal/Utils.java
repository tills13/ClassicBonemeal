package com.jseb.classicbonemeal;

import org.bukkit.CropState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.material.Crops;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Tree;	
import org.bukkit.TreeType;
import org.bukkit.TreeSpecies;
import org.bukkit.World;

import java.util.Random;

public class Utils {
	/* growCrop(Block block)
	 * this method will grow a crop from seeds
	 *
	 * block - the block which contains the seeds
	 * returns the success of the operation.
	 */
	public static boolean growCrop(Block block) {
		Crops crop = new Crops(block.getTypeId(), block.getData());
		if (crop.getState() != CropState.RIPE) {
			crop.setState(CropState.RIPE);
			block.setData(crop.getData());
			return true;
		} 

		return false;
	}

	/* growTree(Block block)
	 * this method will generate a tree at the location of the block
	 *
	 * block - the block which contains the sapling to grow the tree
	 * returns the success of the operation.
	 */
	public static boolean growTree(Block block) {
		Byte data = block.getData();
		int id = block.getTypeId();
		Tree tree = new Tree(block.getTypeId(), block.getData());
		TreeSpecies species = tree.getSpecies();

		TreeType type = null;

		if (species.equals(TreeSpecies.BIRCH)) type = (new Random().nextDouble() < ClassicBonemeal.mega_tree_prob) ? TreeType.TALL_BIRCH : TreeType.BIRCH;
		else if (species.equals(TreeSpecies.ACACIA)) type = TreeType.ACACIA;
		else if (species.equals(TreeSpecies.DARK_OAK)) type = TreeType.DARK_OAK;
		else if (species.equals(TreeSpecies.JUNGLE)) return generateLargeTree(block, TreeType.SMALL_JUNGLE, TreeType.JUNGLE);
		else if (species.equals(TreeSpecies.REDWOOD)) return generateLargeTree(block, (new Random().nextDouble() < ClassicBonemeal.mega_tree_prob) ? TreeType.TALL_REDWOOD : TreeType.REDWOOD, TreeType.MEGA_REDWOOD);
		else if (species.equals(TreeSpecies.GENERIC))  type = (new Random().nextDouble() < ClassicBonemeal.mega_tree_prob) ? TreeType.BIG_TREE : TreeType.TREE;
		else return false;
	
		block.setType(Material.AIR);
		boolean used = block.getWorld().generateTree(block.getLocation(), type);

		if (!used) block.setTypeIdAndData(id, data, true);
		return used;
	}

	/* growFromSteam(Block block)
	 * this method will grow a steam from seeds, grow a melon/pumpkin from that stem, or grow cocoa plants
	 *
	 * block - the seeds or stem to be operated on
	 * returns the success of the operation.
	 */
	public static boolean growFromStem(Block block) {
		if (block.getType().equals(Material.MELON_STEM) || block.getType().equals(Material.PUMPKIN_STEM)) {
			growCrop(block);
			return placePumpkinOrMelon(block);
		} else {
			CocoaPlant cPlant = (CocoaPlant) block.getState().getData();
			if (cPlant.getSize() != CocoaPlantSize.LARGE) {
				cPlant.setSize(CocoaPlantSize.LARGE);
				block.setTypeIdAndData(cPlant.getItemTypeId(), cPlant.getData(), true);
				return true;
			}
		}

		return false;
	}

	/* generateLargeTree(Block block, TreeType typeSmall, TreeType typeLarge)
	 * this method will generate one of those massive trees
	 *
	 * block - the location of the block that contains one of the saplings
	 * typeSmall - the type of tree to be grown if it can't find a group of four
	 * typeLarge - the type of tree to be grown if it can find a group of four
	 * returns the success of the operation.
	 */
	public static boolean generateLargeTree(Block block, TreeType typeSmall, TreeType typeLarge) {
		// backup failure data
		int typeid = block.getTypeId();
		byte data = block.getData();

		Block [] group = getFourGroup(block);
		Block grow_location = (group == null) ? block : getNorthWestBlock(group[0], group[1], group[2], group[3]);
		TreeType type = (group == null) ? typeSmall : typeLarge;

		if (group == null) block.setType(Material.AIR);
		else for (Block mBlock : group) mBlock.setType(Material.AIR);

		if (!grow_location.getWorld().generateTree(grow_location.getLocation(), type)) {
			if (group == null) block.setTypeIdAndData(typeid, data, true);
			else for (Block mBlock : group) mBlock.setTypeIdAndData(typeid, data, true);

			return false;
		}

		return true;
	} 

	/* getFourGroup(Block block)
	 * this method will attemps to find a group of four saplings used to generate large jungle trees.
	 * in future versions of MineCraft, this function will also be able to determine groups of four saplings in general
	 *
	 * block - the location of the block that was clicked - this block is a sapling
	 * returns null for no group of four found or an array containing the four saplings in the group
	 */
	public static Block[] getFourGroup(Block block) {
		Block[] blocks = {block, null, null, null};

		World world = block.getWorld();
		int x = block.getX(), y = block.getY(), z = block.getZ();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (j == 0 || i == 0) continue;
				blocks[1] = world.getBlockAt(x - i, y, z);
				blocks[2] = world.getBlockAt(x - i, y, z - j);
				blocks[3] = world.getBlockAt(x, y, z - j);

				boolean pass = true;
				for (Block mBlock : blocks) {
					if (!(mBlock.getData() == blocks[0].getData())) {
						pass = false;
						break;
					}
				}

				if (pass) return blocks;
			}
		}

		return null;
	}

	/* getNorthWestBlock(Block b1, Block b2, Block b3, Block b4)
	 * this method will determine the block at the northwest corner of a 2x2 square of blocks
	 *
	 * b1 - b4: the blocks in the square
	 * returns the block at the north west corner
	 */
	public static Block getNorthWestBlock(Block b1, Block b2, Block b3, Block b4) {
		Block grow_location = b1.getRelative(BlockFace.NORTH).equals(b2) ? b2 
							: b1.getRelative(BlockFace.NORTH).equals(b3) ? b3 
							: b1.getRelative(BlockFace.NORTH).equals(b4) ? b4 
							: b1;
		grow_location = grow_location.getRelative(BlockFace.WEST).equals(b2) ? b2 
					  : grow_location.getRelative(BlockFace.WEST).equals(b3) ? b3 
					  : grow_location.getRelative(BlockFace.WEST).equals(b4) ? b4 
					  : grow_location;

		return grow_location;
	}

	/* placeBlock(Block block)
	 * this method will generate the pumpkin or melon from a stem that has been previously grown
	 *
	 * block - the stem of the melon or pumpkin to be placed
	 * returns the success of the operation.
	 */
	public static boolean placePumpkinOrMelon(Block block) {
		World world = block.getWorld();
		BlockFace faces[] = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

		Material block_type = block.getType().equals(Material.PUMPKIN_STEM) ? Material.PUMPKIN : Material.MELON_BLOCK;
		for (BlockFace face : faces) if (block.getRelative(face).getType().equals(block_type)) return false;	
		for (BlockFace face : faces) {
			Block mblock = block.getRelative(face);

			if (mblock.getType().equals(Material.AIR)) {
				if (!(mblock.getRelative(BlockFace.DOWN).getType().equals(Material.DIRT) 
				  || (mblock.getRelative(BlockFace.DOWN).getType().equals(Material.SOIL)) 
				  || (mblock.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS)))) continue;
					
				mblock.setType((block.getType().equals(Material.PUMPKIN_STEM) ? Material.PUMPKIN : Material.MELON_BLOCK));
				return true;
			}
		}

		return false;
	}
}