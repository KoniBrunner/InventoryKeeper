package ch.itship.minecraft.inventorykeeper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.itship.minecraft.common.Utility;

public class InventoryCommands implements CommandExecutor {

	private InventoryKeeper plugin = null;
	private Logger logger = null;

	public InventoryCommands(InventoryKeeper plugin) {
		this.plugin = plugin;
		logger = this.plugin.getLogger();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.DARK_RED + InventoryConfig.getInstance(plugin).getMessage("Err.Args", sender));
			return false;
		}
		if (!(sender instanceof Player) && !(sender instanceof BlockCommandSender)) {
			logger.severe("WTF is a " + sender.getClass().toString());
			return false;
		}
		if (sender instanceof Player && !((Player) sender).hasPermission("inventorykeeper.admin")) {
			((Player) sender).sendMessage(
					ChatColor.DARK_RED + InventoryConfig.getInstance(plugin).getMessage("Err.Permission", sender));
		}
		@SuppressWarnings("deprecation")
		Player target = sender.getServer().getPlayer(args[0]);
		if (target != null) {
			if (!target.hasPermission("inventorykeeper.player")) {
				return false;
			}
			if (command.getName().equalsIgnoreCase("saveinventory")) {
				saveInventory(target);
			} else if (command.getName().equalsIgnoreCase("resetinventory")) {
				resetInventory(target);
			} else if (command.getName().equalsIgnoreCase("restoreinventory")) {
				restoreInventory(target);
			} else {
				logger.severe("WTF I have to do with command " + command.getName());
				return false;
			}
		} else {
			sender.sendMessage(
					ChatColor.DARK_RED + InventoryConfig.getInstance(plugin).getMessage("Err.NoPlayer", sender));
			return false;
		}
		return true;
	}

	private void saveInventory(Player player) {
		if (InventoryConfig.getInstance(plugin).Plugin.getBoolean("SaveOnlyIfNotEmtpy")
				&& Utility.isPlayersInventoryEmpty(player)) {
			return;
		}
		FileConfiguration fc = InventoryConfig.getInstance(plugin).getPlayerConfig(player);
		if (fc.get("Saved") != null) {
			Date lastSaved = (Date) fc.get("Saved");
			if (lastSaved.getTime()
					+ (InventoryConfig.getInstance(plugin).Plugin.getInt("TimeBetweenSaves") * 1000) > (new Date()
							.getTime()))
				return;
		}
		for (int i = 0; i < 5; i++) {
			fc.set("Armor" + (i + 1), fc.get("Armor" + i));
			fc.set("Content" + (i + 1), fc.get("Content" + i));
			fc.set("Spawn" + (i + 1), fc.get("Spawn" + i));
		}
		fc.set("Armor0", Utility.serializeItemStackList(player.getInventory().getArmorContents()));
		fc.set("Content0", Utility.serializeItemStackList(player.getInventory().getContents()));
		if (InventoryConfig.getInstance(plugin).Plugin.getBoolean("SaveSpawnLocation"))
			fc.set("Spawn0", player.getBedSpawnLocation());
		fc.set("Saved", new Date());
		InventoryConfig.getInstance(plugin).savePlayerConfig(player);
	}

	@SuppressWarnings("unchecked")
	private void restoreInventory(Player player) {
		FileConfiguration fc = InventoryConfig.getInstance(plugin).getPlayerConfig(player);
		if (fc.get("Armor0") != null) {
			ItemStack[] deserializedInventory = Utility.deserializeItemStackList(
					(List<HashMap<Map<String, Object>, Map<String, Object>>>) fc.get("Armor0"));
			player.getInventory().setArmorContents(deserializedInventory);
		}
		if (fc.get("Content0") != null) {
			ItemStack[] deserializedInventory = Utility.deserializeItemStackList(
					(List<HashMap<Map<String, Object>, Map<String, Object>>>) fc.get("Content0"));
			player.getInventory().setContents(deserializedInventory);
		}
		if (InventoryConfig.getInstance(plugin).Plugin.getBoolean("SaveSpawnLocation") && fc.get("Spawn0") != null) {
			player.setBedSpawnLocation((org.bukkit.Location) fc.get("Spawn0"), true);
		}
	}

	private void resetInventory(Player player) {
		saveInventory(player);
		player.getInventory().clear();
	}

}
