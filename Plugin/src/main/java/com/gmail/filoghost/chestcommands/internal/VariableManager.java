package com.gmail.filoghost.chestcommands.internal;

import com.gmail.filoghost.chestcommands.api.Variable;
import com.gmail.filoghost.chestcommands.bridge.PlaceholderAPIBridge;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.TokenManagerBridge;
import com.gmail.filoghost.chestcommands.util.BukkitUtils;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VariableManager {

  private static final Pattern pattern = Pattern.compile("[^{]+(?=})");
  private static final Map<String, Variable> variables = Utils.newHashMap();

  static {
    if (PlayerPointsBridge.hasValidPlugin()) {
      register("points",
          (executor, identifier) -> String.valueOf(PlayerPointsBridge.getPoints(executor)));
    }
    if (TokenManagerBridge.hasValidPlugin()) {
      register("tokens",
          (executor, identifier) -> String.valueOf(TokenManagerBridge.getTokens(executor)));
    }
    if (VaultBridge.hasValidEconomy()) {
      register("money",
          (executor, identifier) -> VaultBridge.formatMoney(VaultBridge.getMoney(executor)));
    }
    if (VaultBridge.hasValidPermission()) {
      register("group", (executor, identifier) -> VaultBridge.getPrimaryGroup(executor));
    }

    register("player", (executor, identifier) -> executor.getName());
    register("online", (executor, identifier) -> String.valueOf(CachedGetters.getOnlinePlayers()));
    register("max_players", (executor, identifier) -> String.valueOf(Bukkit.getMaxPlayers()));
    register("world", (executor, identifier) -> executor.getWorld().getName());
    register("x", (executor, identifier) -> String.valueOf(executor.getLocation().getX()));
    register("y", (executor, identifier) -> String.valueOf(executor.getLocation().getY()));
    register("z", (executor, identifier) -> String.valueOf(executor.getLocation().getZ()));
    register("bed_", ((executor, identifier) -> {
      if (executor.getBedSpawnLocation() == null) {
        return null;
      } else if (identifier.equalsIgnoreCase("world")) {
        return executor.getBedSpawnLocation().getWorld().getName();
      } else if (identifier.equalsIgnoreCase("x")) {
        return String.valueOf(executor.getBedSpawnLocation().getX());
      } else if (identifier.equalsIgnoreCase("y")) {
        return String.valueOf(executor.getBedSpawnLocation().getY());
      } else if (identifier.equalsIgnoreCase("z")) {
        return String.valueOf(executor.getBedSpawnLocation().getZ());
      } else {
        return null;
      }
    }));
    register("exp", (executor, identifier) -> String.valueOf(executor.getTotalExperience()));
    register("level", (executor, identifier) -> String.valueOf(executor.getLevel()));
    register("exp_to_level", (executor, identifier) -> String.valueOf(executor.getExpToLevel()));
    register("food_level", (executor, identifier) -> String.valueOf(executor.getFoodLevel()));
    register("ip", (executor, identifier) -> executor.getAddress().getAddress().getHostAddress());
    register("biome",
        (executor, identifier) -> String.valueOf(executor.getLocation().getBlock().getBiome()));
    register("ping", ((executor, identifier) -> BukkitUtils.getPing(executor)));
    register("rainbow", (executor, identifier) -> {
      ChatColor[] values = ChatColor.values();
      ChatColor color = null;
      while (color == null
          || color.equals(ChatColor.BOLD)
          || color.equals(ChatColor.ITALIC)
          || color.equals(ChatColor.STRIKETHROUGH)
          || color.equals(ChatColor.RESET)
          || color.equals(ChatColor.MAGIC)
          || color.equals(ChatColor.UNDERLINE)) {
        color = values[ThreadLocalRandom.current().nextInt(values.length - 1)];
      }
      return FormatUtils.addColors("&" + color.getChar());
    });
  }

  private VariableManager() {

  }

  public static void register(String prefix, Variable variable) {
    variables.put(prefix, variable);
  }

  public static boolean hasVariables(String message) {
    if (message == null) {
      return false;
    }
    Pattern prefixPattern = Pattern.compile("(" + String.join("|", variables.keySet()) + ").*");
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group().trim();
      if (prefixPattern.matcher(identifier).find()) {
        return true;
      }
    }
    return PlaceholderAPIBridge.hasValidPlugin() && PlaceholderAPIBridge.hasPlaceholders(message);
  }

  public static String setVariables(String message, Player executor) {
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group().trim();
      for (Map.Entry<String, Variable> variable : variables.entrySet()) {
        if (identifier.startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.replace(variable.getKey(), ""));
          if (replace != null) {
            message = message.replace("{" + identifier + "}", replace);
          }
        }
      }
    }
    if (PlaceholderAPIBridge.hasValidPlugin()) {
      message = PlaceholderAPIBridge.setPlaceholders(message, executor);
    }
    return message;
  }
}
