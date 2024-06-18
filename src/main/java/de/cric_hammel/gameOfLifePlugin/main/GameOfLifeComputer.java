package de.cric_hammel.gameOfLifePlugin.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class GameOfLifeComputer extends BukkitRunnable {

	private Map<String, Block> livingBlocks;
	private Map<String, Block> toBeKilled = new HashMap<>();
	private Set<Block> whiteBlocks = new HashSet<>();
	private Set<Block> toBeBorn = new HashSet<>();
	private World world;
	private GameOfLifePlugin plugin;

	public GameOfLifeComputer(World world) {
		super();
		this.world = world;
		plugin = GameOfLifePlugin.getPlugin();
		livingBlocks = plugin.getLivingBlocks();
	}

	@Override
	public void run() {
		if (world.getPlayers().size() != 0) {
			computeBlackBlocks();
			computeWhiteBlocks();
			renderBlocks();
		} else {
			plugin.setRunning(false);
			this.cancel();
			TextComponent text = Component.text("§cThe §fGame §8of §fLife §cwas stopped because there are no players in the world anymore!");
			Bukkit.getServer().broadcast(text);
		}
	}

	private void computeBlackBlocks() {
		for (Entry<String, Block> entry : livingBlocks.entrySet()) {
			Block livingBlock = entry.getValue();
			int noOfLivingNeighbors = this.getNoOfLivingNeighbors(livingBlock, true);
			if (noOfLivingNeighbors < 2 || noOfLivingNeighbors > 3) {
				String key = createKeyForLocation(livingBlock.getLocation());
				toBeKilled.put(key, livingBlock);
			}
		}
	}

	private void computeWhiteBlocks() {
		for (Block block : whiteBlocks) {
			int noOfLivingNeighbors = this.getNoOfLivingNeighbors(block, false);
			if (noOfLivingNeighbors == 3) {
				toBeBorn.add(block);
			}
		}
		whiteBlocks.clear();
	}

	private void renderBlocks() {
		for (Map.Entry<String, Block> entry : toBeKilled.entrySet()) {
			entry.getValue().setType(Material.WHITE_CONCRETE);
			livingBlocks.remove(entry.getKey());
		}
		toBeKilled.clear();
		for (Block block : toBeBorn) {
			block.setType(Material.BLACK_CONCRETE);
			String key = createKeyForLocation(block.getLocation());
			livingBlocks.put(key, block);
		}
		toBeBorn.clear();
	}

	private int getNoOfLivingNeighbors(Block livingBlock, boolean calledForLivingBlock) {
		int count = 0;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) {
					continue;
				}
				Block b = world.getBlockAt(livingBlock.getLocation().getBlockX() + x, 1,
						livingBlock.getLocation().getBlockZ() + z);
				if (b.getType() == Material.BLACK_CONCRETE) {
					count++;
				} else if (calledForLivingBlock && b.getType() == Material.WHITE_CONCRETE) {
					whiteBlocks.add(b);
				}

			}
		}
		return count;
	}

	private String createKeyForLocation(Location currentLocation) {
		return new StringBuilder().append(currentLocation.getBlockX()).append("_").append(currentLocation.getBlockZ())
				.toString();
	}
}
