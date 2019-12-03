package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.bridge.TitleBridge;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import org.bukkit.entity.Player;

public class TitleIconCommand extends IconCommand {

  private String title;
  private String subtitle = " ";
  private int fadeIn = 20;
  private int fadeOut = 60;
  private int stay = 20;

  public TitleIconCommand(String command) {
    super(command);
    if (!hasVariables) {
      parseTitle(super.command);
    }
  }

  private void parseTitle(String command) {
    title = command;

    String[] split = command.split("\\|", 5); // Max of 5 pieces
    if (split.length == 2) {
      title = split[0];
      subtitle = split[1];
    } else if (split.length == 5 && Utils.isValidPositiveInteger(split[2].trim()) && Utils
        .isValidPositiveInteger(split[3].trim()) && Utils.isValidPositiveInteger(split[4].trim())) {
      title = split[0];
      subtitle = split[1];
      fadeIn = Integer.parseInt(split[2].trim());
      stay = Integer.parseInt(split[3].trim());
      fadeOut = Integer.parseInt(split[4].trim());
    }

    title = FormatUtils.addColors(title);
    subtitle = FormatUtils.addColors(subtitle);
  }

  @Override
  public void execute(Player player, TaskChain<?> taskChain) {
    if (hasVariables) {
      parseTitle(getParsedCommand(player));
    }

    taskChain.sync(() -> TitleBridge.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut));
  }
}
