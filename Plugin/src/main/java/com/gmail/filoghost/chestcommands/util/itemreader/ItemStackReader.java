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
package com.gmail.filoghost.chestcommands.util.itemreader;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.MaterialsRegistry;
import com.gmail.filoghost.chestcommands.util.StringUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.Validate;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.BaseColorParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.ColorParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.EnchantmentParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.FireworkParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.FlagParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.LoreParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.NameParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.PatternParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.PotionParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.SkullParser;
import com.gmail.filoghost.chestcommands.util.itemreader.parser.UnbreakableParser;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackReader {

  private static Map<Pattern, ItemParser> parserMap = Utils.newHashMap();

  static {
    registerParser("base-color:", new BaseColorParser());
    registerParser("unbreakable", new UnbreakableParser());
    registerParser("lore:", new LoreParser());
    registerParser("name:", new NameParser());
    registerParser("skull:", new SkullParser());
    registerParser("pattern:", new PatternParser());
    registerParser("firework:", new FireworkParser());
    registerParser("enchant:", new EnchantmentParser());
    registerParser("effect:", new PotionParser());
    registerParser("flag:", new FlagParser());
    registerParser("color:", new ColorParser());
  }

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
        for (Entry<Pattern, ItemParser> entry : parserMap.entrySet()) {
          Matcher matcher = entry.getKey().matcher(data);
          if (matcher.find()) {
            entry.getValue().parse(this, matcher.replaceFirst("").trim());
          }
        }
      }
    }
  }

  public static void registerParser(String regex, ItemParser parser) {
    Pattern pattern = Pattern
        .compile("^(?i)" + regex); // Case insensitive and only at the beginning
    parserMap.put(pattern, parser);
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

  public ItemMeta getItemMeta() {
    return itemMeta;
  }

  public void setItemMeta(ItemMeta newMeta) {
    this.itemMeta = newMeta;
  }

  public void setUnbreakable() {
    this.unbreakable = true;
  }

  public ItemStack createStack() {
    ItemStack item = new ItemStack(material, amount, dataValue);
    item.setItemMeta(itemMeta);
    if (unbreakable) {
      item = ItemUtils.setUnbreakable(item);
    }
    return item;
  }
}
