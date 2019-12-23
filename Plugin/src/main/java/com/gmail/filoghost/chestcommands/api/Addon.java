package com.gmail.filoghost.chestcommands.api;

import com.gmail.filoghost.chestcommands.ChestCommands;

public abstract class Addon {
  private String name;

  public Addon(String name) {
    this.name = name;
  }

  public abstract void onEnable();

  public abstract void onDisable();

  protected ChestCommands getPlugin() {
    return ChestCommands.getInstance();
  }

  public String getName() {
    return name;
  }
}
