package com.gmail.filoghost.chestcommands.bridge.currency;

import com.gmail.filoghost.chestcommands.util.MenuUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// PlayerPoints minimum version: 2.0
public class PlayerPointsBridge {

  private static PlayerPoints playerPoints;

  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
      return false;
    }

    Plugin pointsPlugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");

    if (pointsPlugin == null) {
      return false;
    }

    playerPoints = (PlayerPoints) pointsPlugin;
    return true;
  }

  public static boolean hasValidPlugin() {
    return playerPoints != null;
  }

  public static int getPoints(Player player) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("PlayerPoints plugin was not found!");
    }
    return playerPoints.getAPI().look(player.getUniqueId());
  }

  public static boolean hasPoints(Player player, int minimum) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("PlayerPoints plugin was not found!");
    }
    if (minimum < 0) {
      throw new IllegalArgumentException("Invalid amount of points: " + minimum);
    }

    return playerPoints.getAPI().look(player.getUniqueId()) >= minimum;
  }

  /**
   * @return true if the operation was successful.
   */
  public static boolean takePoints(Player player, int points) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("PlayerPoints plugin was not found!");
    }
    if (points < 0) {
      throw new IllegalArgumentException("Invalid amount of points: " + points);
    }

    boolean result = playerPoints.getAPI().take(player.getUniqueId(), points);

    MenuUtils.refreshMenu(player);

    return result;
  }


  public static boolean givePoints(Player player, int points) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("PlayerPoints plugin was not found!");
    }
    if (points < 0) {
      throw new IllegalArgumentException("Invalid amount of points: " + points);
    }

    boolean result = playerPoints.getAPI().give(player.getUniqueId(), points);

    MenuUtils.refreshMenu(player);

    return result;
  }

}
