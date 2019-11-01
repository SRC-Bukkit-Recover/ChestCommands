package com.gmail.filoghost.chestcommands.bridge.heads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class HeadsPlusBridge {

  private static HeadsPlus plugin;

  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("HeadsPlus")) {
      return false;
    }

    Plugin headsPlus = Bukkit.getServer().getPluginManager().getPlugin("HeadsPlus");

    if (headsPlus == null) {
      return false;
    }

    plugin = ((HeadsPlus) headsPlus);

    return true;
  }

  public static boolean hasValidPlugin() {
    return plugin != null;
  }

  public static boolean hasValidID(String input) {
    return hasValidPlugin() && getItem(input) != null;
  }

  public static ItemStack getItem(String input) {
    try {
      return plugin.getHeadsXConfig().getSkull(input);
    } catch (NullPointerException e) {
      return null;
    }
  }
}
