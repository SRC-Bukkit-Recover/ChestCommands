package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneyIconRequirement extends IconRequirement {

  public MoneyIconRequirement() {
    super(true, ValueType.NUMBER);
  }

  @Override
  public boolean check(Player player) {
    List<Object> values = getParsedValue(player);
    if (values.isEmpty()) {
      return false;
    }
    for (Object value : values) {
      double moneyPrice = ((BigDecimal) value).doubleValue();
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
    getParsedValue(player).forEach(value -> {
      if (!VaultBridge.takeMoney(player, ((BigDecimal) value).doubleValue())) {
        player.sendMessage(ChatColor.RED
            + "Error: the transaction couldn't be executed. Please inform the staff.");
      }
    });
  }
}
