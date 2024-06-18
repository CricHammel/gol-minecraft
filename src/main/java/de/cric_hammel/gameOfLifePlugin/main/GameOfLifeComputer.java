package de.cric_hammel.gameOfLifePlugin.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameOfLifeComputer extends BukkitRunnable {

	private Map<String, Block> livingBlocks;
	private Map<String, Block> toBeKilled = new HashMap<>();
	private Set<Block> deadBlocks = new HashSet<>();
	private Set<Block> toBeBorn = new HashSet<>();
	private World world;
	private GameOfLifePlugin plugin;
	private int gens = 0;

	public GameOfLifeComputer(World world) {
		super();
		this.world = world;
		plugin = GameOfLifePlugin.getPlugin();
		livingBlocks = plugin.getLivingBlocks();
	}
	
	public GameOfLifeComputer(World world, int gens) {
		this(world);
		this.gens = gens;
	}

	@Override
	public void run() {
		
		if (world.getPlayers().size() != 0) {
			
			if (livingBlocks.isEmpty()) {
				world.getPlayers().forEach(p -> p.sendMessage("§cThe §fGame §8of §fLife §cwas stopped after §2" + gens + " §agenerations §cbecause there are no living blocks left!"));
				plugin.setRunning(false);
				this.cancel();
				return;
			}
			
			computeLivingBlocks();
			computeDeadBlocks();
			renderBlocks();
			gens++;
			world.getPlayers().forEach(p -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§2Generations: §a" + gens)));
		} else {
			Bukkit.getServer().broadcastMessage("§cThe §fGame §8of §fLife §cwas stopped after §2" + gens + " §agenerations §cbecause there are no players in the world anymore!");
			plugin.setRunning(false);
			this.cancel();
		}
	}

	private void computeLivingBlocks() {
		
		for (Entry<String, Block> entry : livingBlocks.entrySet()) {
			Block livingBlock = entry.getValue();
			int noOfLivingNeighbors = getNoOfLivingNeighbors(livingBlock, true);
			
			if (noOfLivingNeighbors < 2 || noOfLivingNeighbors > 3) {
				toBeKilled.put(entry.getKey(), livingBlock);
			}
		}
	}

	private void computeDeadBlocks() {
		
		for (Block block : deadBlocks) {
			int noOfLivingNeighbors = getNoOfLivingNeighbors(block, false);
			
			if (noOfLivingNeighbors == 3) {
				toBeBorn.add(block);
			}
		}
		
		deadBlocks.clear();
	}

	private void renderBlocks() {
		
		for (Map.Entry<String, Block> entry : toBeKilled.entrySet()) {
			entry.getValue().setType(Material.WHITE_CONCRETE);
			livingBlocks.remove(entry.getKey());
		}
		
		toBeKilled.clear();
		
		for (Block block : toBeBorn) {
			block.setType(Material.BLACK_CONCRETE);
			String key = GameOfLifePlugin.createKeyForLocation(block.getLocation());
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
				
				Block b = world.getBlockAt(livingBlock.getLocation().getBlockX() + x, -63,
						livingBlock.getLocation().getBlockZ() + z);
				
				if (b.getType() == Material.BLACK_CONCRETE) {
					count++;
				} else if (calledForLivingBlock && b.getType() == Material.WHITE_CONCRETE) {
					deadBlocks.add(b);
				}
			}
		}
		
		return count;
	}
	
	public int getGens() {
		return gens;
	}
}
