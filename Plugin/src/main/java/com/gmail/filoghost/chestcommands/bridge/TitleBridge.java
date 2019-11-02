package com.gmail.filoghost.chestcommands.bridge;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import com.connorlinfoot.titleapi.TitleAPI;
import de.Herbystar.TTA.TTA_Methods;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleBridge {

  private static PluginType type;
  private static boolean isEnabled = false;

  private static TitleManagerAPI titleManagerAPI;

  public static boolean setupPlugin() {
    if (Bukkit.getServer().getPluginManager().isPluginEnabled("TitleManager")) {
      type = PluginType.TITLE_MANAGER;
      titleManagerAPI = (TitleManagerAPI) Bukkit.getPluginManager().getPlugin("TitleManager");
      isEnabled = true;
      return true;
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("TitleAPI")) {
      type = PluginType.TITLE_API;
      isEnabled = true;
      return true;
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("TTA")) {
      type = PluginType.TTA;
      isEnabled = true;
      return true;
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("BountifulAPI")) {
      type = PluginType.BOUNTIFUL_API;
      isEnabled = true;
      return true;
    } else {
      return false;
    }
  }

  public static boolean hasValidPlugin() {
    return isEnabled;
  }

  public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay,
      int fadeOut) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("Title plugins were not found!");
    }
    if (type == PluginType.TITLE_MANAGER) {
      titleManagerAPI.sendTitle(player, title, fadeIn, stay, fadeOut);
      titleManagerAPI.sendSubtitle(player, subtitle, fadeIn, stay, fadeOut);
    } else if (type == PluginType.TITLE_API) {
      TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    } else if (type == PluginType.TTA) {
      TTA_Methods
          .sendTitle(player, title, fadeIn, stay, fadeOut, subtitle, fadeIn, stay, fadeOut);
    } else if (type == PluginType.BOUNTIFUL_API) {
      BountifulAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }
  }

  private enum PluginType {
    TITLE_MANAGER,
    TITLE_API,
    BOUNTIFUL_API,
    TTA
  }
}
