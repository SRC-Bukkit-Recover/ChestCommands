package com.gmail.filoghost.chestcommands.bridge;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import com.connorlinfoot.titleapi.TitleAPI;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.util.Title;
import de.Herbystar.TTA.TTA_Methods;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleBridge {

  private static PluginType type;

  private static TitleManagerAPI titleManagerAPI;

  public static void setupPlugin() {
    if (Bukkit.getServer().getPluginManager().isPluginEnabled("TitleManager")) {
      type = PluginType.TITLE_MANAGER;
      titleManagerAPI = (TitleManagerAPI) Bukkit.getPluginManager().getPlugin("TitleManager");
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("TitleAPI")) {
      type = PluginType.TITLE_API;
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("TTA")) {
      type = PluginType.TTA;
    } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("BountifulAPI")) {
      type = PluginType.BOUNTIFUL_API;
    } else {
      type = PluginType.INTERNAL;
    }
  }

  public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay,
      int fadeOut) {
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
    } else if (type == PluginType.INTERNAL) {
      try {
        Title.send(player, title, subtitle, fadeIn, stay, fadeOut);
      } catch (Exception e) {
        ChestCommands.getInstance().getLogger().log(Level.WARNING, "Error when sending titles", e);
      }
    }
  }

  private enum PluginType {
    TITLE_MANAGER,
    TITLE_API,
    BOUNTIFUL_API,
    TTA,
    INTERNAL
  }
}
