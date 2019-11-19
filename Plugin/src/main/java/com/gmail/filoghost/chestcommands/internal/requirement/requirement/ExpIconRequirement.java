package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpIconRequirement extends IconRequirement {

  public ExpIconRequirement() {
    super(false);
  }

  @Override
  public boolean check(Player player) {
    int expLevelsPrice;
    String parsedExp = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedExp)) {
      expLevelsPrice = ExpressionUtils.getResult(parsedExp).intValue();
    } else {
      try {
        expLevelsPrice = Integer.parseInt(parsedExp);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedExp + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return false;
      }
    }
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
    int expLevelsPrice;
    String parsedExp = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedExp)) {
      expLevelsPrice = ExpressionUtils.getResult(parsedExp).intValue();
    } else {
      try {
        expLevelsPrice = Integer.parseInt(parsedExp);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedExp + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return;
      }
    }
    player.setLevel(player.getLevel() - expLevelsPrice);
  }
}
