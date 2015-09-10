package ch.itship.minecraft.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 * Helper utilities used in monecraft plugins
 *
 * @author Ardamedonner
 * @version 1.0
 */

public class Utility {

	public static String getLanguage(Player p) {
		Object ep;
		try {
			ep = getMethod("getHandle", p.getClass()).invoke(p, (Object[]) null);
			Field f = ep.getClass().getDeclaredField("locale");
			f.setAccessible(true);
			String language = (String) f.get(ep);
			return language;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "en_US";
	}

	private static Method getMethod(String name, Class<?> clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}

	public static String serializeObject(Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			byte[] yourBytes = bos.toByteArray();
			char[] encodedBytes = Base64Coder.encode(yourBytes);
			return new String(encodedBytes);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
			}
			try {
				bos.close();
			} catch (IOException ex) {
			}
		}
		return null;
	}

	public static Object deserializeObject(String serializedString) {
		byte[] decodedBytes = Base64Coder.decode(serializedString.toCharArray());
		ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		return null;
	}

	// credits to
	// https://github.com/zant95/HorseStables/blob/master/src/com/gmail/zant95/HorseStables/SerializeItemStackList.java
	public static List<HashMap<Map<String, Object>, Map<String, Object>>> serializeItemStackList(
			final ItemStack[] itemStackList) {
		final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();

		for (ItemStack itemStack : itemStackList) {
			Map<String, Object> serializedItemStack, serializedItemMeta;
			HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<Map<String, Object>, Map<String, Object>>();

			if (itemStack == null)
				itemStack = new ItemStack(Material.AIR);
			serializedItemMeta = (itemStack.hasItemMeta()) ? itemStack.getItemMeta().serialize() : null;
			itemStack.setItemMeta(null);
			serializedItemStack = itemStack.serialize();

			serializedMap.put(serializedItemStack, serializedItemMeta);
			serializedItemStackList.add(serializedMap);
		}
		return serializedItemStackList;
	}

	// credits to
	// https://github.com/zant95/HorseStables/blob/master/src/com/gmail/zant95/HorseStables/SerializeItemStackList.java
	public static ItemStack[] deserializeItemStackList(
			final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList) {
		final ItemStack[] itemStackList = new ItemStack[serializedItemStackList.size()];

		int i = 0;
		for (HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackMap : serializedItemStackList) {
			Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackMap.entrySet()
					.iterator().next();

			ItemStack itemStack = ItemStack.deserialize(serializedItemStack.getKey());
			if (serializedItemStack.getValue() != null) {
				ItemMeta itemMeta = (ItemMeta) ConfigurationSerialization.deserializeObject(
						serializedItemStack.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
				itemStack.setItemMeta(itemMeta);
			}

			itemStackList[i++] = itemStack;
		}
		return itemStackList;
	}

	public static boolean isPlayersInventoryEmpty(Player player) {
		for (ItemStack item : player.getInventory().getContents())
		{
			if (item != null) return false;
		}
		for (ItemStack item : player.getInventory().getArmorContents())
		{
			if (item.getAmount()>0) return false;
		}
		return true;
	}

}
