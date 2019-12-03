package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConditionIconCommand extends IconCommand {

  public ConditionIconCommand(String command) {
    super(command);
  }

  @Override
  public void execute(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    if (!ExpressionUtils.isBoolean(parsed)) {
      player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
      return;
    }

    if (ExpressionUtils.getResult(parsed).intValue() != 1) {
      taskChain.sync(TaskChain::abort);
    }
  }

}
