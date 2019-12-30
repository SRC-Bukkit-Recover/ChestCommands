package com.gmail.filoghost.chestcommands.api.event;

import com.gmail.filoghost.chestcommands.internal.icon.ExtendedIcon;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IconPreCreateEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final ExtendedIcon extendedIcon;
  private final ConfigurationSection configurationSection;
  private final ErrorLogger errorLogger;
  private final String iconName;
  private final String menuFileName;

  public IconPreCreateEvent(ExtendedIcon extendedIcon, String iconName, String menuFileName,
      ConfigurationSection configurationSection, ErrorLogger errorLogger) {
    this.extendedIcon = extendedIcon;
    this.configurationSection = configurationSection;
    this.errorLogger = errorLogger;
    this.iconName = iconName;
    this.menuFileName = menuFileName;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public ExtendedIcon getExtendedIcon() {
    return extendedIcon;
  }

  public ConfigurationSection getConfigurationSection() {
    return configurationSection;
  }

  public ErrorLogger getErrorLogger() {
    return errorLogger;
  }

  public String getIconName() {
    return iconName;
  }

  public String getMenuFileName() {
    return menuFileName;
  }
}
