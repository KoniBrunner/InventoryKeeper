package ch.itship.minecraft.inventorykeeper;

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryKeeper extends JavaPlugin implements Listener {

	public Permission PlayerPerm = new Permission("inventorykeeper.player");
	public Permission AdminPerm = new Permission("inventorykeeper.admin");
	public final Logger logger = this.getLogger();

	@Override
	public void onEnable() {
		super.onEnable();
		configurePlugin();
		configureCommands();
		logger.info("Version " + getDescription().getVersion() + " enabled!");
	}

	@Override
	public void onDisable() {
		super.onDisable();
		logger.info("Disabled!");
	}

	private void configurePlugin() {
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		InventoryConfig.getInstance(this);
	}

	private void configureCommands() {
		InventoryCommands commands = new InventoryCommands(this);
		this.getCommand("saveinventory").setExecutor(commands);
		this.getCommand("resetinventory").setExecutor(commands);
		this.getCommand("restoreinventory").setExecutor(commands);
	}

}
