package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneyIconRequirement extends IconRequirement {

  public MoneyIconRequirement() {
    super(true);
  }

  @Override
  public boolean check(Player player) {
    double moneyPrice;
    String parsedMoney = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedMoney)) {
      moneyPrice = ExpressionUtils.getResult(parsedMoney).doubleValue();
    } else {
      try {
        moneyPrice = Double.parseDouble(parsedMoney);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedMoney + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return false;
      }
    }
    if (moneyPrice > 0) {
      if (!VaultBridge.hasValidEconomy()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
        return false;
      }

      if (!VaultBridge.hasMoney(player, moneyPrice)) {
        if (failMessage != null) {
          player.sendMessage(failMessage
              .replace("{money}", VaultBridge.formatMoney(moneyPrice)));
        } else {
          player.sendMessage(ChestCommands.getLang().no_money
              .replace("{money}", VaultBridge.formatMoney(moneyPrice)));
        }
        return false;
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    if (!VaultBridge.hasValidEconomy()) {
      player.sendMessage(ChatColor.RED
          + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
      return;
    }
    double moneyPrice;
    String parsedMoney = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedMoney)) {
      moneyPrice = ExpressionUtils.getResult(parsedMoney).doubleValue();
    } else {
      try {
        moneyPrice = Double.parseDouble(parsedMoney);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedMoney + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return;
      }
    }
    VaultBridge.takeMoney(player, moneyPrice);
  }
}
