package com.gmail.filoghost.chestcommands.internal;

import com.gmail.filoghost.chestcommands.bridge.PlaceholderAPIBridge;
import org.bukkit.entity.Player;

public class VariableManager {

  private VariableManager() {

  }

  public static boolean hasVariables(String message) {
    if (message == null) {
      return false;
    }
    for (Variable variable : Variable.values()) {
      if (message.contains(variable.getText())) {
        return true;
      }
    }
    return PlaceholderAPIBridge.hasValidPlugin() && PlaceholderAPIBridge.hasPlaceholders(message);
  }

  public static String setVariables(String message, Player executor) {
    for (Variable variable : Variable.values()) {
      if (message.contains(variable.getText())) {
        message = message.replace(variable.getText(), variable.getReplacement(executor));
      }
    }
    if (PlaceholderAPIBridge.hasValidPlugin()) {
      message = PlaceholderAPIBridge.setPlaceholders(message, executor);
    }
    return message;
  }

}
