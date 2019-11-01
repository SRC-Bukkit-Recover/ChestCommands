package com.gmail.filoghost.chestcommands.util;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.bridge.heads.EpicHeadsBridge;
import com.gmail.filoghost.chestcommands.bridge.heads.HeadDatabaseBridge;
import com.gmail.filoghost.chestcommands.bridge.heads.HeadsPlusBridge;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
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
        setSkullWithURL((SkullMeta) itemMeta, skullOwner);
      } else {
        ((SkullMeta) itemMeta).setOwner(skullOwner);
      }
      // In case the meta has lore, remove it
      itemMeta.setLore(Utils.newArrayList());
    }
    return itemMeta;
  }

  public static void setSkullWithURL(SkullMeta skullMeta, String url) {
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    byte[] encodedData = Base64.getEncoder()
        .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
    try {
      Field profileField;
      profileField = skullMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(skullMeta, profile);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
      ChestCommands.getInstance().getLogger()
          .log(Level.FINE, "Unexpected error when getting skull", e);
    }
  }
}
