package de.cric_hammel.gameOfLifePlugin.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class GameOfLifeCommand implements TabExecutor {

	private GameOfLifeComputer computer;
	private GameOfLifePlugin plugin = GameOfLifePlugin.getPlugin();
	private int rate = 10;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission("gameoflife.gol")) {
				
				if (args.length == 0) {
					player.sendMessage("§7-------<§fGame §8of §fLife§7>-------");
					player.sendMessage("");
					player.sendMessage("§7Play Conway's Game of Life!");
					player.sendMessage("");
					player.sendMessage("§7Commands:");
					player.sendMessage("§f/gol§8: Shows this menu");
					player.sendMessage("§f/gol tp§8: Teleports you to the world of the Game of Life and back");
					player.sendMessage("§f/gol start§8: Starts the Game of Life");
					player.sendMessage("§f/gol stop§8: Stops the Game of Life");
					player.sendMessage("§f/gol clear§8: Clears all the living fields");
					player.sendMessage("§f/gol rate 1-20§8: Sets the rate at which the Game of Life is updated per second (1-20)");
					player.sendMessage("");
					player.sendMessage("§7-------<§fGame §8of §fLife§7>-------");
				} else if (args.length == 1) {
					
					if (args[0].equalsIgnoreCase("tp")) {
						
						if (player.getWorld().getName()
								.equals(Bukkit.getWorld(GameOfLifePlugin.WORLD_NAME).getName())) {
							World mainworld = Bukkit.getWorld(getLevelname());
							player.teleport(mainworld.getSpawnLocation());
							player.sendMessage("§2You got teleported to §7" + mainworld.getName() + " §2!");
						} else {
							player.teleport(Bukkit.getWorld(GameOfLifePlugin.WORLD_NAME).getSpawnLocation());
							player.sendMessage("§2You got teleported to the world of the §fGame §8of §fLife §2!");
						}
					} else if (args[0].equalsIgnoreCase("start")) {
						
						if (!plugin.isRunning()) {
							computer = new GameOfLifeComputer(Bukkit.getWorld(GameOfLifePlugin.WORLD_NAME));
							computer.runTaskTimer(plugin, 0, rate);
							plugin.setRunning(true);
							player.sendMessage("§2Started the §fGame §8of §fLife §2!");
						} else {
							player.sendMessage("§cThe §fGame §8of §fLife §cis already running!");
						}
					} else if (args[0].equalsIgnoreCase("stop")) {
						
						if (plugin.isRunning()) {
							computer.cancel();
							plugin.setRunning(false);
							player.sendMessage("§2Stopped the §fGame §8of §fLife §2after §a" + computer.getGens() + " §2generations §2!");
						} else {
							player.sendMessage("§cThe §fGame §8of §fLife §cis not running!");
						}
					} else if (args[0].equalsIgnoreCase("clear")) {
						
						if (!plugin.isRunning()) {
							Map<String, Block> livingBlocks;
							livingBlocks = plugin.getLivingBlocks();
							
							for (Block block : livingBlocks.values()) {
								block.setType(Material.WHITE_CONCRETE);
							}
							
							livingBlocks.clear();
							player.sendMessage("§2Cleared all living fields!");
						} else {
							player.sendMessage("§cThe §fGame §8of §fLife §cis running, please stop it first!");
						}
					} else if (args[0].equalsIgnoreCase("rate")) {
						player.sendMessage("§2The current rate is §a" + rate + "§2.");
						player.sendMessage("§2To change it, run this command followed by the rate (1-20)");
					}
				} else if (args.length == 2 && args[0].equalsIgnoreCase("rate")) {
					int newRate;
					
					try {
						newRate = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("§cThis is not a number! Please input a number between 1 and 20");
						return false;
					}
					
					if (newRate < 1 || newRate > 20) {
						player.sendMessage("§cThis is not a valid number! Please input a number between 1 and 20");
						return false;
					}
					
					rate = newRate;
					
					if (plugin.isRunning()) {
						computer.cancel();
						computer = new GameOfLifeComputer(Bukkit.getWorld(GameOfLifePlugin.WORLD_NAME), computer.getGens());
						computer.runTaskTimer(plugin, 1, rate);
					}
					
					player.sendMessage("§2Set the rate to §a" + rate + "§2!");
				} else
					player.sendMessage("§cToo many arguments! Use §6/gol [tp,start,stop,clear]§c!");
			} else
				player.sendMessage("§cYou don't have the permission to execute this command!");
		} else
			sender.sendMessage("This command can only be executed by a player!");
		return true;
	}

	public String getLevelname() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("server.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop.getProperty("level-name");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completions = new ArrayList<String>();
		
		if (args.length == 1) {
			completions.add("tp");
			completions.add("start");
			completions.add("stop");
			completions.add("clear");
			completions.add("rate");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("rate")) {
			completions.add("10");
		}
		
		return completions;
	}
}
