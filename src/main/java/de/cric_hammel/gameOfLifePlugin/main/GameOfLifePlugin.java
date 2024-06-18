package de.cric_hammel.gameOfLifePlugin.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GameOfLifePlugin extends JavaPlugin {

	private static GameOfLifePlugin plugin;

	private Map<String, Block> livingBlocks = new HashMap<>();

	public static final String WORLD_NAME = "Game_Of_Life";

	private boolean isRunning = false;

	public void onEnable() {
		plugin = this;
		livingBlocks.clear();
		ConsoleCommandSender cSender = Bukkit.getConsoleSender();
		cSender.sendMessage("-------<Game of Life>-------");
		cSender.sendMessage("|");
		cSender.sendMessage("| Author: Cric_Hammel");
		cSender.sendMessage("| Version: 1.0");
		cSender.sendMessage("|");
		cSender.sendMessage("-------<Game of Life>-------");
		cSender.sendMessage("Creating world...");
		WorldCreator golWorld = new WorldCreator(WORLD_NAME);
		golWorld.environment(Environment.NORMAL);
		golWorld.generateStructures(false);
		golWorld.type(WorldType.FLAT);
		golWorld.generatorSettings(
				"{\"structures\": {\"structures\": {}}, \"layers\": [{\"block\": \"bedrock\", \"height\": 1}, {\"block\": \"white_concrete\", \"height\": 1}], \"biome\":\"the_void\"}");

		golWorld.createWorld().save();

		getCommand("gol").setExecutor(new GameOfLifeCommand());

		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new BlockDestroyListener(), this);

	}

	public void onDisable() {
		livingBlocks.clear();
		ConsoleCommandSender cSender = Bukkit.getConsoleSender();
		cSender.sendMessage("Deleting world...");
		World golWorld = Bukkit.getWorld(WORLD_NAME);
		if (golWorld != null) {
			Bukkit.unloadWorld(golWorld, true);
			File worldFolder = golWorld.getWorldFolder();
			deleteDirectory(worldFolder);
		}
		cSender.sendMessage("Goodbye!");
	}

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	public static GameOfLifePlugin getPlugin() {
		return plugin;
	}

	public Map<String, Block> getLivingBlocks() {
		return livingBlocks;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
