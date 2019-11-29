package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import java.math.BigDecimal;
import org.bukkit.entity.Player;

public class ExpLevelIconRequirement extends IconRequirement {

  public ExpLevelIconRequirement() {
    super(false, ValueType.NUMBER);
  }

  @Override
  public boolean check(Player player) {
    if (getParsedValue(player) == null) {
      return false;
    }
    int expLevelsPrice = ((BigDecimal) getParsedValue(player)).intValue();
    if (expLevelsPrice > 0 && player.getLevel() < expLevelsPrice) {
      if (failMessage != null) {
        player.sendMessage(
            failMessage.replace("{levels}", Integer.toString(expLevelsPrice)));
      } else {
        player.sendMessage(
            ChestCommands.getLang().no_exp.replace("{levels}", Integer.toString(expLevelsPrice)));
      }
      return false;
    }
    return true;
  }

  @Override
  public void take(Player player) {
    player.setLevel(player.getLevel() - ((BigDecimal) getParsedValue(player)).intValue());
  }
}
