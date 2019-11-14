package com.gmail.filoghost.chestcommands.util;

import com.gmail.filoghost.chestcommands.ChestCommands;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.entity.Player;

public class Title {

  private static Class<?> chatBaseComponentClass;
  private static Class<?> playOutTitleClass;
  private static Method chatTitleMethod;
  private static Constructor titleConstructor;
  private static Constructor timingTitleConstructor;

  static {
    try {
      chatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
      playOutTitleClass = NMSUtils.getNMSClass("PacketPlayOutTitle");
      titleConstructor = playOutTitleClass.getConstructor(
          playOutTitleClass.getDeclaredClasses()[0],
          chatBaseComponentClass,
          int.class, int.class, int.class);
      chatTitleMethod = chatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class);
      timingTitleConstructor = playOutTitleClass
          .getConstructor(
              playOutTitleClass.getDeclaredClasses()[0],
              chatBaseComponentClass,
              int.class, int.class, int.class);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      ChestCommands.getInstance().getLogger().log(Level.WARNING, "Error when setting titles", e);
    }
  }

  private Title() {

  }

  public static void send(Player player, String title, String subtitle, int fadeInTime,
      int showTime, int fadeOutTime)
      throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
    Object chatTitle = chatTitleMethod.invoke(null, "{\"text\": \"" + title + "\"}");
    Object packet = titleConstructor.newInstance(
        playOutTitleClass.getDeclaredClasses()[0].getField("TITLE")
            .get(null), chatTitle,
        fadeInTime, showTime, fadeOutTime);

    Object chatsTitle = chatTitleMethod.invoke(null, "{\"text\": \"" + subtitle + "\"}");
    Object timingPacket = timingTitleConstructor.newInstance(
        playOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE")
            .get(null), chatsTitle,
        fadeInTime, showTime, fadeOutTime);

    NMSUtils.sendPacket(player, packet);
    NMSUtils.sendPacket(player, timingPacket);
  }
}
