package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.entity.Player;

public class ExpLevelIconRequirement extends IconRequirement {

  public ExpLevelIconRequirement() {
    super(false, ValueType.NUMBER);
  }

  @Override
  public boolean check(Player player) {
    List<Object> values = getParsedValue(player);
    if (values.isEmpty()) {
      return false;
    }
    for (Object value : values) {
      int expLevelsPrice = ((BigDecimal) value).intValue();
      if (expLevelsPrice > 0 && player.getLevel() < expLevelsPrice) {
        if (failMessage != null) {
          if (!failMessage.isEmpty()) {
            player.sendMessage(
                failMessage.replace("{levels}", Integer.toString(expLevelsPrice)));
          }
        } else {
          if (!ChestCommands.getLang().no_exp.isEmpty()) {
            player.sendMessage(
                ChestCommands.getLang().no_exp
                    .replace("{levels}", Integer.toString(expLevelsPrice)));
          }
        }
        return false;
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    getParsedValue(player)
        .forEach(value -> player.setLevel(player.getLevel() - ((BigDecimal) value).intValue()));
  }
}
