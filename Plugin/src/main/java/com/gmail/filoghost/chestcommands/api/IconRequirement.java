package com.gmail.filoghost.chestcommands.api;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.internal.VariableManager;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class IconRequirement {

  private final ValueType valueType;
  protected String failMessage;
  protected List<String> values;
  private boolean canTake;

  public IconRequirement(boolean canTake, ValueType valueType) {
    this.canTake = canTake;
    this.valueType = valueType;
  }

  protected List<Object> getParsedValue(Player player) {
    List<Object> parsedValues = Utils.newArrayList();
    this.values.forEach(string -> {
      String parsed =
          VariableManager.hasVariables(string) ? VariableManager.setVariables(string, player)
              : string;
      switch (valueType) {
        case NUMBER:
          if (ExpressionUtils.isValidExpression(parsed)) {
            parsedValues.add(ExpressionUtils.getResult(parsed));
          } else {
            try {
              parsedValues.add(BigDecimal.valueOf(Long.parseLong(parsed)));
            } catch (NumberFormatException e) {
              String error =
                  ChatColor.RED + "Error parsing value!" + parsed + " is not a valid number";
              player.sendMessage(error);
              ChestCommands.getInstance().getLogger().warning(error);
            }
          }
          break;
        case STRING:
          parsedValues.add(parsed);
          break;
        case BOOLEAN:
          if (!ExpressionUtils.isBoolean(parsed)) {
            player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
            parsedValues.add(false);
          } else {
            parsedValues.add(ExpressionUtils.getResult(parsed).intValue() == 1);
          }
          break;
      }
    });
    return parsedValues;
  }

  public abstract boolean check(Player player);

  public abstract void take(Player player);

  public void setFailMessage(String message) {
    this.failMessage = message;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public void setValues(String input) {
    List<String> list = Arrays.asList(input.split(";"));
    list.replaceAll(String::trim);
    setValues(list);
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
