package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import java.math.BigDecimal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneyIconRequirement extends IconRequirement {

  public MoneyIconRequirement() {
    super(true, ValueType.NUMBER);
  }

  @Override
  public boolean check(Player player) {
    if (getParsedValue(player) == null) {
      return false;
    }
    double moneyPrice = ((BigDecimal) getParsedValue(player)).doubleValue();
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
    if (!VaultBridge.takeMoney(player, ((BigDecimal) getParsedValue(player)).doubleValue())) {
      player.sendMessage(ChatColor.RED
          + "Error: the transaction couldn't be executed. Please inform the staff.");
    }
  }
}
