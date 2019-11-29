package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import org.bukkit.entity.Player;

public class CloseMenuCommand extends IconCommand {

  public CloseMenuCommand(String command) {
    super(command);
  }

  @Override
  public void execute(Player player, TaskChain taskChain) {
    if (Boolean.parseBoolean(getParsedCommand(player))) {
      taskChain.sync(player::closeInventory);
    }
  }
}
