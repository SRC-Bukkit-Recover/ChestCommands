package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PointIconRequirement extends IconRequirement {

  public PointIconRequirement() {
    super(true, ValueType.NUMBER);
  }

  @Override
  public boolean check(Player player) {
    List<Object> values = getParsedValue(player);
    if (values.isEmpty()) {
      return false;
    }
    for (Object value : values) {
      int playerPointsPrice = ((BigDecimal) value).intValue();
      if (playerPointsPrice > 0) {
        if (!PlayerPointsBridge.hasValidPlugin()) {
          player.sendMessage(ChatColor.RED
              + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
          return false;
        }

        if (!PlayerPointsBridge.hasPoints(player, playerPointsPrice)) {
          if (failMessage != null) {
            if (!failMessage.isEmpty()) {
              player.sendMessage(failMessage
                  .replace("{points}", Integer.toString(playerPointsPrice)));
            }
          } else {
            if (!ChestCommands.getLang().no_points.isEmpty()) {
              player.sendMessage(ChestCommands.getLang().no_points
                  .replace("{points}", Integer.toString(playerPointsPrice)));
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    if (!PlayerPointsBridge.hasValidPlugin()) {
      player.sendMessage(ChatColor.RED
          + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
      return;
    }
    getParsedValue(player).forEach(value -> {
      if (!PlayerPointsBridge.takePoints(player, ((BigDecimal) value).intValue())) {
        player.sendMessage(ChatColor.RED
            + "Error: the transaction couldn't be executed. Please inform the staff.");
      }
    });
  }
}
