package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PointIconRequirement extends IconRequirement {

  public PointIconRequirement() {
    super(true);
  }

  @Override
  public boolean check(Player player) {
    int playerPointsPrice;
    String parsedPoints = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedPoints)) {
      playerPointsPrice = ExpressionUtils.getResult(parsedPoints).intValue();
    } else {
      try {
        playerPointsPrice = Integer.parseInt(parsedPoints);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedPoints + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return false;
      }
    }
    if (playerPointsPrice > 0) {
      if (!PlayerPointsBridge.hasValidPlugin()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
        return false;
      }

      if (!PlayerPointsBridge.hasPoints(player, playerPointsPrice)) {
        if (failMessage != null) {
          player.sendMessage(failMessage
              .replace("{points}", Integer.toString(playerPointsPrice)));
        } else {
          player.sendMessage(ChestCommands.getLang().no_points
              .replace("{points}", Integer.toString(playerPointsPrice)));
        }
        return false;
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
    int playerPointsPrice;
    String parsedPoints = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedPoints)) {
      playerPointsPrice = ExpressionUtils.getResult(parsedPoints).intValue();
    } else {
      try {
        playerPointsPrice = Integer.parseInt(parsedPoints);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedPoints + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return;
      }
    }
    if (!PlayerPointsBridge.takePoints(player, playerPointsPrice)) {
      player.sendMessage(ChatColor.RED
          + "Error: the transaction couldn't be executed. Please inform the staff.");
    }
  }
}
