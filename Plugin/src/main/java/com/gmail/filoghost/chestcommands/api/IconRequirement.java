package com.gmail.filoghost.chestcommands.api;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.internal.VariableManager;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import java.math.BigDecimal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class IconRequirement {

  protected String failMessage;
  protected String value;
  private final ValueType valueType;
  private boolean canTake;

  public IconRequirement(boolean canTake, ValueType valueType) {
    this.canTake = canTake;
    this.valueType = valueType;
  }

  protected Object getParsedValue(Player player) {
    String parsed =
        VariableManager.hasVariables(value) ? VariableManager.setVariables(value, player) : value;
    switch (valueType) {
      case NUMBER:
        if (ExpressionUtils.isValidExpression(parsed)) {
          return ExpressionUtils.getResult(parsed);
        } else {
          try {
            return BigDecimal.valueOf(Long.parseLong(parsed));
          } catch (NumberFormatException e) {
            String error =
                ChatColor.RED + "Error parsing value!" + parsed + " is not a valid number";
            player.sendMessage(error);
            ChestCommands.getInstance().getLogger().warning(error);
            return null;
          }
        }
      case STRING:
        return parsed;
      case BOOLEAN:
        if (!ExpressionUtils.isBoolean(parsed)) {
          player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
          return false;
        } else {
          return ExpressionUtils.getResult(parsed).intValue() == 1;
        }
      default:
        return null;
    }
  }

  public abstract boolean check(Player player);

  public abstract void take(Player player);

  public void setFailMessage(String message) {
    this.failMessage = message;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void canTake(boolean canTake) {
    this.canTake = canTake;
  }

  public boolean canTake() {
    return canTake;
  }

  protected enum ValueType {
    STRING,
    BOOLEAN,
    NUMBER
  }
}
