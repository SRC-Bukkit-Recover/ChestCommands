package com.gmail.filoghost.chestcommands.bridge.currency;

import com.gmail.filoghost.chestcommands.util.MenuUtils;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TokenManagerBridge {

  private static TokenManager tokenManager;

  private TokenManagerBridge() {

  }

  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) {
      return false;
    }

    Plugin tokenPlugin = Bukkit.getPluginManager().getPlugin("TokenManager");

    if (tokenPlugin == null) {
      return false;
    }

    tokenManager = (TokenManager) tokenPlugin;
    return true;
  }

  public static boolean hasValidPlugin() {
    return tokenManager != null;
  }

  public static long getTokens(Player player) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("TokenManager plugin was not found!");
    }
    return tokenManager.getTokens(player).orElse(0);
  }

  public static boolean hasTokens(Player player, long minimum) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("TokenManager plugin was not found!");
    }
    if (minimum < 0) {
      throw new IllegalArgumentException("Invalid amount of tokens: " + minimum);
    }

    return tokenManager.getTokens(player).orElse(0) >= minimum;
  }

  public static boolean takeTokens(Player player, long tokens) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("TokenManager plugin was not found!");
    }
    if (tokens < 0) {
      throw new IllegalArgumentException("Invalid amount of tokens: " + tokens);
    }

    return tokenManager.removeTokens(player, tokens);
  }


  public static boolean giveTokens(Player player, long tokens) {
    if (!hasValidPlugin()) {
      throw new IllegalStateException("TokenManager plugin was not found!");
    }
    if (tokens < 0) {
      throw new IllegalArgumentException("Invalid amount of tokens: " + tokens);
    }

    boolean result = tokenManager.addTokens(player, tokens);

    MenuUtils.refreshMenu(player);

    return result;
  }

}
