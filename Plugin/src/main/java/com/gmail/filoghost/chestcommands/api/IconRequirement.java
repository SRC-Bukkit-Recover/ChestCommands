package com.gmail.filoghost.chestcommands.api;

import com.gmail.filoghost.chestcommands.internal.VariableManager;
import org.bukkit.entity.Player;

public abstract class IconRequirement {

  protected String failMessage;
  protected String value;
  private boolean canTake;

  public IconRequirement(boolean canTake) {
    this.canTake = canTake;
  }

  public String getParsedValue(Player player) {
    return VariableManager.hasVariables(value) ? VariableManager.setVariables(value, player)
        : value;
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
}
