package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.internal.icon.IconCommand;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GivePointsIconCommand extends IconCommand {

  private String errorMessage;

  public GivePointsIconCommand(String command) {
    super(command);
  }

  @Override
  public void execute(Player player, TaskChain taskChain) {
    int pointsToGive = 0;
    String parsed = getParsedCommand(player);
    if (Utils.isValidPositiveInteger(parsed)) {
      pointsToGive = Integer.parseInt(parsed);
    } else if (ExpressionUtils.isValidExpression(parsed)) {
      pointsToGive = Objects.requireNonNull(ExpressionUtils.getResult(parsed)).intValue();
    } else {
      errorMessage = ChatColor.RED + "Invalid points amount: " + command;
    }

    if (errorMessage != null) {
      player.sendMessage(errorMessage);
      return;
    }
    if (!PlayerPointsBridge.hasValidPlugin()) {
      player.sendMessage(
          ChatColor.RED + "The plugin PlayerPoints was not found. Please inform the staff.");
      return;
    }

    if (pointsToGive > 0) {
      int finalPointsToGive = pointsToGive;
      taskChain.sync(() -> {
        if (!PlayerPointsBridge.givePoints(player, finalPointsToGive)) {
          player.sendMessage(ChatColor.RED
              + "Error: the transaction couldn't be executed. Please inform the staff.");
        }
      });
    }
  }

}
