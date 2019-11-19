package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.bridge.currency.TokenManagerBridge;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TokenIconRequirement extends IconRequirement {

  public TokenIconRequirement() {
    super(true);
  }

  @Override
  public boolean check(Player player) {
    long tokenManagerPrice;
    String parsedTokens = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedTokens)) {
      tokenManagerPrice = ExpressionUtils.getResult(parsedTokens).longValue();
    } else {
      try {
        tokenManagerPrice = Long.parseLong(parsedTokens);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedTokens + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return false;
      }
    }
    if (tokenManagerPrice > 0) {
      if (!TokenManagerBridge.hasValidPlugin()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price in tokens, but the plugin TokenManager was not found. For security, the command has been blocked. Please inform the staff.");
        return false;
      }

      if (!TokenManagerBridge.hasTokens(player, tokenManagerPrice)) {
        if (failMessage != null) {
          player.sendMessage(failMessage
              .replace("{tokens}", Long.toString(tokenManagerPrice)));
        } else {
          player.sendMessage(ChestCommands.getLang().no_tokens
              .replace("{tokens}", Long.toString(tokenManagerPrice)));
        }
        return false;
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    if (!TokenManagerBridge.hasValidPlugin()) {
      player.sendMessage(ChatColor.RED
          + "This command has a price in tokens, but the plugin TokenManager was not found. For security, the command has been blocked. Please inform the staff.");
      return;
    }
    long tokenManagerPrice;
    String parsedTokens = getParsedValue(player);
    if (ExpressionUtils.isValidExpression(parsedTokens)) {
      tokenManagerPrice = ExpressionUtils.getResult(parsedTokens).longValue();
    } else {
      try {
        tokenManagerPrice = Long.parseLong(parsedTokens);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedTokens + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return;
      }
    }
    if (!TokenManagerBridge.takeTokens(player, tokenManagerPrice)) {
      player.sendMessage(ChatColor.RED
          + "Error: the transaction couldn't be executed. Please inform the staff.");
    }
  }
}
