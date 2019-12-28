package com.gmail.filoghost.chestcommands.util;

public class VersionUtils {
  private VersionUtils() {

  }

  public static boolean isSpigot() {
    return Utils.isClassLoaded("org.bukkit.entity.Player$Spigot");
  }
}
