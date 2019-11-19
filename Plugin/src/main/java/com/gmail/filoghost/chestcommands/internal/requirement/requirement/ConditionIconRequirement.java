package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConditionIconRequirement extends IconRequirement {

  public ConditionIconRequirement() {
    super(false);
  }

  @Override
  public boolean check(Player player) {
    if (!hasRequirement(player)) {
      if (failMessage != null) {
        player.sendMessage(failMessage);
      } else {
        player.sendMessage(ChestCommands.getLang().default_no_requirement_message);
      }
      return false;
    }
    return true;
  }

  private boolean hasRequirement(Player player) {
    String parsed = getParsedValue(player);

    if (!ExpressionUtils.isBoolean(parsed)) {
      player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
      return false;
    }

    return ExpressionUtils.getResult(parsed).intValue() == 1;
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
