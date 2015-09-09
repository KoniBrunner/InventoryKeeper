package ch.itship.minecraft.inventorykeeper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ch.itship.minecraft.common.Utility;

public class InventoryConfig {

	private static InventoryConfig instance = null;
	private InventoryKeeper plugin = null;
	private Logger logger = null;
	private Map<String, FileConfiguration> playerConfigs = new HashMap<String, FileConfiguration>();

	private FileConfiguration Messages = null;
	public FileConfiguration Plugin = null;

	protected InventoryConfig(InventoryKeeper plugin) {
		this.plugin = plugin;
		logger = this.plugin.getLogger();
		createPlugin();
		createPlayerData();
		createMessages();
	}

	public static InventoryConfig getInstance(InventoryKeeper plugin) {
		if (instance == null) {
			instance = new InventoryConfig(plugin);
		}
		return instance;
	}

	public static void Reset() {
		instance = null;
	}

	private void createPlugin() {
		Plugin = this.plugin.getConfig();
		
		boolean changedSomething = false;
		if (!Plugin.contains("ConfigVersion")) {
			Plugin.createSection("ConfigVersion");
			Plugin.set("ConfigVersion", 1.0);
			changedSomething = true;
		}
		if (!Plugin.contains("AutoUpdate")) {
			Plugin.createSection("AutoUpdate");
			Plugin.set("AutoUpdate", true);
			changedSomething = true;
		}
		if (!Plugin.contains("UpdateNotification")) {
			Plugin.createSection("UpdateNotification");
			Plugin.set("UpdateNotification", true);
			changedSomething = true;
		}
		if (!Plugin.contains("Metrics")) {
			Plugin.createSection("Metrics");
			Plugin.set("Metrics", true);
			changedSomething = true;
		}
		if (!Plugin.contains("TimeBetweenSaves")) {
			Plugin.createSection("TimeBetweenSaves");
			Plugin.set("TimeBetweenSaves", 10);
			changedSomething = true;
		}
		if (!Plugin.contains("SaveOnlyIfNotEmtpy")) {
			Plugin.createSection("SaveOnlyIfNotEmtpy");
			Plugin.set("SaveOnlyIfNotEmtpy", true);
			changedSomething = true;
		}
		if (!Plugin.contains("SaveSpawnLocation")) {
			Plugin.createSection("SaveSpawnLocation");
			Plugin.set("SaveSpawnLocation", true);
			changedSomething = true;
		}
		
		if (changedSomething) {
			plugin.saveConfig();
		}
	}

	private void createMessages() {
		File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "messages.yml");
		Messages = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			logger.info("Creating Messages configuration file!");
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.severe("Couldn't create messages file!");
				e.printStackTrace();
			}
		}
		boolean changedSomething = false;
		String[][] messages = new String[][] {
				{ "Err.Args", "False number of arguments!", "Falsche Anzahl Parameter!" },
				{ "Err.NoPlayer", "Can't find the player!", "Kann den Spieler nicht finden!" },
				{ "Err.Permission", "You do not have suffisient permissions to use that command!", "Sie haben nicht die Berechtigungen, dieses Kommando zu benutzen!" } };
		if (!Messages.contains("en_US")) {
			Messages.createSection("en_US");
			changedSomething = true;
		}
		if (!Messages.contains("de_DE")) {
			Messages.createSection("de_DE");
			changedSomething = true;
		}
		for (int s = 0; s < messages.length; s++) {
			if (!Messages.contains("en_US." + messages[s][0])) {
				Messages.createSection("en_US." + messages[s][0]);
				changedSomething = true;
			}
			if (Messages.get("en_US." + messages[s][0]) != null
					&& !Messages.getString("en_US." + messages[s][0]).isEmpty()) {
				Messages.set("en_US." + messages[s][0], messages[s][1]);
				changedSomething = true;
			}
			if (!Messages.contains("de_DE." + messages[s][0])) {
				Messages.createSection("de_DE." + messages[s][0]);
				changedSomething = true;
			}
			if (Messages.get("de_DE." + messages[s][0]) != null
					&& !Messages.getString("de_DE." + messages[s][0]).isEmpty()) {
				Messages.set("de_DE." + messages[s][0], messages[s][2]);
				changedSomething = true;
			}
		}

		if (changedSomething) {
			try {
				Messages.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getMessage(String key, CommandSender sender) {
		String lang = "en_US";
		if (sender instanceof Player) {
			String lng = Utility.getLanguage((Player) sender);
			if (lng.equals("de_DE"))
				lang = "de_DE";
		}
		if (Messages.get(lang + "." + key) != null)
			return Messages.getString(lang + "." + key);
		return "";
	}

	private void createPlayerData() {
		File playerDataDir = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
		if (!playerDataDir.exists()) {
			logger.info("Creating PlayerData Directory!");
			try {
				boolean done = playerDataDir.mkdir();
				if (!done)
					throw new IOException("Can't create directory " + playerDataDir.getAbsolutePath());
			} catch (Exception e) {
				logger.severe("Couldn't create PlayerData directory!");
				e.printStackTrace();
			}
		}
	}

	public void savePlayerConfig(Player player) {
		if (playerConfigs.containsKey(player.getUniqueId().toString())) {
			File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator
					+ player.getUniqueId().toString() + ".yml");
			try {
				playerConfigs.get(player.getUniqueId().toString()).save(file);
			} catch (IOException e) {
				logger.severe("Couldn't save player file!");
				e.printStackTrace();
			}
		}
	}

	public FileConfiguration getPlayerConfig(Player player) {
		if (playerConfigs.containsKey(player.getUniqueId().toString())) {
			return playerConfigs.get(player.getUniqueId().toString());
		} else {
			File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator
					+ player.getUniqueId().toString() + ".yml");
			FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);

			if (!file.exists()) {
				logger.info("Creating Player(" + player.getUniqueId().toString() + ") configuration file!");
				try {
					file.createNewFile();
				} catch (IOException e) {
					logger.severe("Couldn't create player file!");
					e.printStackTrace();
				}
			}

			boolean changedSomething = false;
			if (!playerConfig.contains("Name")) {
				playerConfig.createSection("Name");
				playerConfig.set("Name", player.getName());
				changedSomething = true;
			}
			if (!playerConfig.contains("UUID")) {
				playerConfig.createSection("UUID");
				playerConfig.set("UUID", player.getUniqueId().toString());
				changedSomething = true;
			}
			if (!playerConfig.contains("Saved")) {
				playerConfig.createSection("Saved");
				Date dt = new Date();
				dt.setTime(0);
				playerConfig.set("Saved", dt);
				changedSomething = true;
			}
			for (int i = 0; i < 6; i++) {
				if (!playerConfig.contains("Armor" + i)) {
					playerConfig.createSection("Armor" + i);
					changedSomething = true;
				}
				if (!playerConfig.contains("Content" + i)) {
					playerConfig.createSection("Content" + i);
					changedSomething = true;
				}
				if (!playerConfig.contains("Spawn" + i)) {
					playerConfig.createSection("Spawn" + i);
					changedSomething = true;
				}
			}
			if (!playerConfig.getString("Name").equals(player.getName())) {
				playerConfig.set("Name", player.getName());
				changedSomething = true;
			}

			if (changedSomething) {
				try {
					playerConfig.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			playerConfigs.put(player.getUniqueId().toString(), playerConfig);

			return playerConfig;
		}
	}

}
