package com.gmail.filoghost.chestcommands.bridge.heads;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseBridge {

  private static HeadDatabaseAPI api;

  private HeadDatabaseBridge() {

  }

  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase")) {
      return false;
    }

    api = new HeadDatabaseAPI();

    return true;
  }

  public static boolean hasValidPlugin() {
    return api != null;
  }

  public static ItemStack getItem(String input) {
    try {
      return api.getItemHead(input);
    } catch (NullPointerException e) {
      return null;
    }
  }
}
