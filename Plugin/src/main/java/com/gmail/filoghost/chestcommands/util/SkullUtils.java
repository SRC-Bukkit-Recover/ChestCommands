package com.gmail.filoghost.chestcommands.util;

import com.gmail.filoghost.chestcommands.bridge.EpicHeadsBridge;
import com.gmail.filoghost.chestcommands.bridge.HeadDatabaseBridge;
import com.gmail.filoghost.chestcommands.bridge.HeadsPlusBridge;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullUtils {

  public static ItemMeta parseSkull(ItemMeta itemMeta, String skullOwner) {
    if (itemMeta instanceof SkullMeta) {
      if (skullOwner.startsWith("hdb-") && HeadDatabaseBridge
          .hasValidID(skullOwner.replace("hdb-", ""))) {
        itemMeta = HeadDatabaseBridge.getItem(skullOwner.replace("hdb-", "")).getItemMeta();
      } else if (skullOwner.startsWith("hp-") && HeadsPlusBridge
          .hasValidID(skullOwner.replace("hp-", ""))) {
        itemMeta = HeadsPlusBridge.getItem(skullOwner.replace("hp-", "")).getItemMeta();
      } else if (skullOwner.startsWith("eh-") && EpicHeadsBridge
          .hasValidID(skullOwner.replace("eh-", ""))) {
        itemMeta = EpicHeadsBridge.getItem(skullOwner.replace("eh-", "")).getItemMeta();
      } else if (Utils.isValidURL(skullOwner)) {
        itemMeta = getSkull(skullOwner, (SkullMeta) itemMeta);
      } else {
          ((SkullMeta) itemMeta).setOwner(skullOwner);
      }
      // In case the meta has lore, remove it
      itemMeta.setLore(Utils.newArrayList());
    }
    return itemMeta;
  }

  public static SkullMeta getSkull(String url, SkullMeta skullMeta) {
    if (url == null || url.isEmpty())
      return skullMeta;
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
    Field profileField = null;
    try {
      profileField = skullMeta.getClass().getDeclaredField("profile");
    } catch (NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    }
    profileField.setAccessible(true);
    try {
      profileField.set(skullMeta, profile);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return skullMeta;
  }
}
