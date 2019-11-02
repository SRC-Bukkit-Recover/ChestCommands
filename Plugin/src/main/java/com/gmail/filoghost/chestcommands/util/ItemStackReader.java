/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.chestcommands.util;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.serializer.EnchantmentSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemStackReader {

  private Material material;
  private int amount = 1;
  private ItemMeta itemMeta;
  private short dataValue = 0;
  private boolean unbreakable = false;
  private boolean explicitDataValue = false;

  /**
   * Reads item in the format "id:data, amount [,<itemMeta>]" id can be either the id of the
   * material or its name. for example wool:5, 3 is a valid input.
   */
  public ItemStackReader(String input, boolean parseAmount) throws FormatException {
    Validate.notNull(input, "input cannot be null");

    String[] itemData = new String[0];
    String materialString;
    String amountString = "1";

    // Divide data
    String[] split = input.split(",");
    // Remove spaces, they're not needed
    materialString = StringUtils.stripChars(split[0], " _-");
    if (split.length > 2) {
      itemData = Arrays.copyOfRange(split, 2, split.length);
    }
    if (split.length > 1) {
      // Remove spaces, they're not needed
      amountString = StringUtils.stripChars(split[1], " _-");
    }

    // Read the optional amount
    if (parseAmount) {
      if (!Utils.isValidInteger(amountString)) {
        throw new FormatException("invalid amount \"" + amountString + "\"");
      }

      int anInt = Integer.parseInt(amountString);
      if (anInt <= 0) {
        throw new FormatException("invalid amount \"" + amountString + "\"");
      }
      this.amount = anInt;
    }

    // Read the optional data value
    String[] splitByColons = materialString.split(":");

    if (splitByColons.length > 1) {

      if (!Utils.isValidShort(splitByColons[1])) {
        throw new FormatException("invalid data value \"" + splitByColons[1] + "\"");
      }

      short datavalue = Short.parseShort(splitByColons[1]);
      if (datavalue < 0) {
        throw new FormatException("invalid data value \"" + splitByColons[1] + "\"");
      }

      this.explicitDataValue = true;
      this.dataValue = datavalue;

      // Only keep the first part as input
      materialString = splitByColons[0];
    }

    Material type = MaterialsRegistry.matchMaterial(materialString);

    if (type == null || MaterialsRegistry.isAir(type)) {
      throw new FormatException("invalid material \"" + materialString + "\"");
    }
    this.material = type;

    // Read ItemMeta
    itemMeta = new ItemStack(type, dataValue).getItemMeta().clone();
    if (itemData.length > 0) {
      for (String data : itemData) {
        data = data.trim();
        if (data.toLowerCase().startsWith(Nodes.NAME)) {
          parseDisplayName(data);
        }
        if (data.toLowerCase().startsWith(Nodes.LORE)) {
          parseLore(data);
        }
        if (data.toLowerCase().startsWith(Nodes.COLOR)) {
          parseColor(data);
        }
        if (data.toLowerCase().startsWith(Nodes.POTION)) {
          parsePotion(data);
        }
        if (data.toLowerCase().startsWith(Nodes.BASE_COLOR)) {
          parseBannerBaseColor(data);
        }
        if (data.toLowerCase().startsWith(Nodes.PATTERN)) {
          parseBannerPattern(data);
        }
        if (data.toLowerCase().startsWith(Nodes.ENCHANT)) {
          parseEnchant(data);
        }
        if (data.toLowerCase().startsWith(Nodes.FLAG)) {
          parseItemFlag(data);
        }
        if (data.toLowerCase().startsWith(Nodes.SKULL)) {
          parseSkull(data);
        }
        if (data.toLowerCase().startsWith(Nodes.FIREWORK)) {
          parseFirework(data);
        }
        if (data.equalsIgnoreCase(Nodes.UNBREAKABLE)) {
          unbreakable = true;
        }
      }
    }
  }

  // FIREWORK
  // <type>|<color>|<fade>|<flicker>|<trail>
  //
  // <color> and <fade> : Name or <R>.<G>.<B>
  // <color1>-<color2>-...
  private void parseFirework(String input) throws FormatException {
    String[] split = input.substring(Nodes.FIREWORK.length()).trim().split(" ");
    if (itemMeta instanceof FireworkMeta) {
      List<FireworkEffect> effects = new ArrayList<>();
      for (String firework : split) {
        effects.add(ItemUtils.parseFireworkEffect(firework));
      }
      ((FireworkMeta) itemMeta).addEffects(effects);
    } else if (itemMeta instanceof FireworkEffectMeta) {
      ((FireworkEffectMeta) itemMeta).setEffect(ItemUtils.parseFireworkEffect(split[0]));
    }
  }

  // Skull
  // <skull>
  private void parseSkull(String input) {
    String skullOwner = input.substring(Nodes.SKULL.length()).trim();
    itemMeta = SkullUtils.parseSkull(itemMeta, skullOwner);
  }

  // Item flags
  // <itemflag1> <itemflag2>
  private void parseItemFlag(String input) throws FormatException {
    String flags = input.substring(Nodes.FLAG.length()).trim();
    for (String flag : flags.split(" ")) {
      try {
        itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
      } catch (Exception e) {
        throw new FormatException("invalid item flags \"" + flag + "\"");
      }
    }
  }

  // Banner Pattern
  // <pattern>|<color>
  private void parseBannerPattern(String input) throws FormatException {
    if (itemMeta instanceof BannerMeta) {
      ((BannerMeta) itemMeta).setPatterns(ItemUtils.parseBannerPatternList(
          Arrays
              .asList(
                  input.substring(Nodes.PATTERN.length()).trim().replace("|", ":").split(" "))));
    }
  }

  // Base color
  // <color>
  private void parseBannerBaseColor(String input) throws FormatException {
    if (itemMeta instanceof BannerMeta) {
      ((BannerMeta) itemMeta)
          .setBaseColor(ItemUtils.parseDyeColor(input.substring(Nodes.BASE_COLOR.length()).trim()));
    }
  }

  // Enchantment
  // <enchant>|<level>
  private void parseEnchant(String input) throws FormatException {
    String enchants = input.substring(Nodes.ENCHANT.length()).trim();
    for (String enchant : enchants.split(" ")) {
      String[] data = enchant.split("\\|");
      if (data.length != 2) {
        throw new FormatException("invalid enchant format \"" + input + "\"");
      }
      if (!Utils.isValidInteger(data[1])) {
        throw new FormatException("invalid integer \"" + input + "\"");
      }
      if (EnchantmentSerializer.matchEnchantment(data[0]) == null) {
        throw new FormatException("invalid enchant type \"" + input + "\"");
      }
      itemMeta
          .addEnchant(EnchantmentSerializer.matchEnchantment(data[0]), Integer.parseInt(data[1]),
              true);
    }
  }

  // Potion
  // <effect>|<duration>|<power>
  private void parsePotion(String input) throws FormatException {
    String potions = input.substring(Nodes.POTION.length()).trim();
    if (itemMeta instanceof PotionMeta) {
      for (String potion : potions.split(" ")) {
        String[] data = potion.split("\\|");
        if (data.length != 3) {
          throw new FormatException("invalid potion format \"" + input + "\"");
        }
        if (!(Utils.isValidInteger(data[1]) || Utils.isValidInteger(data[2]))) {
          throw new FormatException("invalid integer \"" + input + "\"");
        }
        if (PotionEffectType.getByName(data[0]) == null) {
          throw new FormatException("invalid effect type \"" + input + "\"");
        }
        ((PotionMeta) itemMeta).addCustomEffect(
            new PotionEffect(PotionEffectType.getByName(data[0]), Integer.parseInt(data[1]),
                Integer.parseInt(data[2])), true);
      }
    }
  }

  // Color
  // <RED> <GREEN> <BLUE>
  private void parseColor(String input) throws FormatException {
    if (itemMeta instanceof LeatherArmorMeta) {
      ((LeatherArmorMeta) itemMeta).setColor(
          ItemUtils.parseColor(input.substring(Nodes.COLOR.length()).trim().replace(" ", ",")));
    }
  }

  // Lore
  // <Lore1> <Lore2>
  // Example: This_is_line_1 This_is_line_2
  private void parseLore(String input) {
    List<String> lore = new ArrayList<>();
    for (String line : input.substring(Nodes.LORE.length()).trim().split(" ")) {
      lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("_", " ")));
    }
    itemMeta.setLore(lore);
  }

  // Display Name
  // <display_name>
  // Example: This_Is_Example
  private void parseDisplayName(String input) {
    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
        input.substring(Nodes.NAME.length()).trim().replace("_", " ")));
  }

  public Material getMaterial() {
    return material;
  }

  public int getAmount() {
    return amount;
  }

  public short getDataValue() {
    return dataValue;
  }

  public boolean hasExplicitDataValue() {
    return explicitDataValue;
  }

  public ItemStack createStack() {
    ItemStack item = new ItemStack(material, amount, dataValue);
    item.setItemMeta(itemMeta);
    if (unbreakable) {
      item = ItemUtils.setUnbreakable(item);
    }
    return item;
  }

  public Set<ItemFlag> getItemFlags() {
    return itemMeta.getItemFlags();
  }

  public List<String> getLore() {
    return itemMeta.getLore();
  }

  public String getDisplayName() {
    return itemMeta.getDisplayName();
  }

  public Map<Enchantment, Integer> getEnchants() {
    return itemMeta.getEnchants();
  }

  public List<PotionEffect> getEffects() {
    return ((PotionMeta) itemMeta).getCustomEffects();
  }

  private class Nodes {

    static final String LORE = "lore:";
    static final String NAME = "name:";
    static final String SKULL = "skull:";
    static final String PATTERN = "pattern:";
    static final String BASE_COLOR = "base-color:";
    static final String ENCHANT = "enchant:";
    static final String POTION = "effect:";
    static final String FLAG = "flag:";
    static final String COLOR = "color:";
    static final String FIREWORK = "firework:";
    static final String UNBREAKABLE = "unbreakable";
  }
}
