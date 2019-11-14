package com.gmail.filoghost.chestcommands.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public class Title {

  private Title() {

  }

  public static void send(Player player, String title, String subtitle, int fadeInTime,
      int showTime, int fadeOutTime)
      throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
    Object chatTitle = NMSUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
        .getMethod("a", String.class)
        .invoke(null, "{\"text\": \"" + title + "\"}");
    Constructor<?> titleConstructor = NMSUtils.getNMSClass("PacketPlayOutTitle").getConstructor(
        NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
        NMSUtils.getNMSClass("IChatBaseComponent"),
        int.class, int.class, int.class);
    Object packet = titleConstructor.newInstance(
        NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE")
            .get(null), chatTitle,
        fadeInTime, showTime, fadeOutTime);

    Object chatsTitle = NMSUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
        .getMethod("a", String.class)
        .invoke(null, "{\"text\": \"" + subtitle + "\"}");
    Constructor<?> timingTitleConstructor = NMSUtils.getNMSClass("PacketPlayOutTitle")
        .getConstructor(
            NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
            NMSUtils.getNMSClass("IChatBaseComponent"),
            int.class, int.class, int.class);
    Object timingPacket = timingTitleConstructor.newInstance(
        NMSUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE")
            .get(null), chatsTitle,
        fadeInTime, showTime, fadeOutTime);

    NMSUtils.sendPacket(player, packet);
    NMSUtils.sendPacket(player, timingPacket);
  }
}
