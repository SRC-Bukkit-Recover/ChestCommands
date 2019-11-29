package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DelayIconCommand extends IconCommand {

  private String errorMessage;
  private int delay;

  public DelayIconCommand(String command) {
    super(command);

    if (!Utils.isValidPositiveInteger(command)) {
      errorMessage = ChatColor.RED + "Invalid delay: " + command;
      return;
    }

    delay = Integer.parseInt(command);
  }

  @Override
  public void execute(Player player, TaskChain taskChain) {
    if (errorMessage != null) {
      player.sendMessage(errorMessage);
      return;
    }

    taskChain.delay(delay);
  }
}
