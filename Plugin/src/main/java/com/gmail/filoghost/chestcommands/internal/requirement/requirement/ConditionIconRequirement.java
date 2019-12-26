package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import org.bukkit.entity.Player;

public class ConditionIconRequirement extends IconRequirement {

  public ConditionIconRequirement() {
    super(false, ValueType.BOOLEAN);
  }

  @Override
  public boolean check(Player player) {
    if (getParsedValue(player).contains(Boolean.FALSE)) {
      if (failMessage != null) {
        player.sendMessage(failMessage);
      } else {
        player.sendMessage(ChestCommands.getLang().default_no_requirement_message);
      }
      return false;
    }
    return true;
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
