package de.cric_hammel.gameOfLifePlugin.main;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockDestroyListener implements Listener {

	@EventHandler
	public void onPlayerDestroyBlockEvent(BlockBreakEvent event) {
		GameOfLifePlugin plugin = GameOfLifePlugin.getPlugin();
		Player p = event.getPlayer();
		World currentWorld = p.getWorld();
		Block block = event.getBlock();
		Map<String, Block> livingBlocks = plugin.getLivingBlocks();
		Location currentLocation = block.getLocation().add(0.5, 0.5, 0.5);

		if (currentWorld.getName().equals(GameOfLifePlugin.WORLD_NAME)) {
			
			if (block.getType() == Material.WHITE_CONCRETE) {
				event.setCancelled(true);
				block.setType(Material.BLACK_CONCRETE);
				String key = GameOfLifePlugin.createKeyForLocation(currentLocation);
				livingBlocks.put(key, block);
			} else if (block.getType() == Material.BLACK_CONCRETE) {
				event.setCancelled(true);
				block.setType(Material.WHITE_CONCRETE);
				String key = GameOfLifePlugin.createKeyForLocation(currentLocation);
				livingBlocks.remove(key);
			}
		}
	}
}
