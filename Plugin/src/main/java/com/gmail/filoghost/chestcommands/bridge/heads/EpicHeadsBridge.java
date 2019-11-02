package com.gmail.filoghost.chestcommands.bridge.heads;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EpicHeadsBridge {

  private static HeadManager manager;

  private EpicHeadsBridge() {

  }

  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("EpicHeads")) {
      return false;
    }

    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("EpicHeads");

    if (plugin == null) {
      return false;
    }

    manager = ((EpicHeads) plugin).getHeadManager();

    return true;
  }

  public static boolean hasValidPlugin() {
    return manager != null;
  }

  public static boolean hasValidID(String input) {
    return hasValidPlugin() && getItem(input) != null;
  }

  public static ItemStack getItem(String input) {
    try {
      String[] split = input.split("-");
      int id;
      List<Head> heads;
      if (split.length == 1) {
        id = Integer.parseInt(split[0]);
        heads = manager.getHeads();
      } else {
        id = Integer.parseInt(split[1]);
        if (split[0].equalsIgnoreCase("global")) {
          heads = manager.getGlobalHeads();
        } else if (split[0].equalsIgnoreCase("local")) {
          heads = manager.getLocalHeads();
        } else {
          return null;
        }
      }
      Optional<Head> head = heads.stream().filter(h -> h.getId() == id).findFirst();
      return head.map(Head::asItemStack).orElse(null);
    } catch (NullPointerException | NumberFormatException e) {
      return null;
    }
  }
}
