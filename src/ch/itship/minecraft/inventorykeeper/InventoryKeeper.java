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
		getLogger().info("Enabled!");

	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled!");

	}
}
