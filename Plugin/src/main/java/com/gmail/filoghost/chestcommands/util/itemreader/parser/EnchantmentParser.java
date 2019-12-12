package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.serializer.EnchantmentSerializer;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    ItemMeta itemMeta = reader.getItemMeta();
    for (String enchant : value.split(" ")) {
      String[] data = enchant.split("\\|");
      if (data.length != 2) {
        throw new FormatException("invalid enchant format \"" + value + "\"");
      }
      if (!Utils.isValidInteger(data[1])) {
        throw new FormatException("invalid integer \"" + value + "\"");
      }
      if (EnchantmentSerializer.matchEnchantment(data[0]) == null) {
        throw new FormatException("invalid enchant type \"" + value + "\"");
      }
      if (itemMeta instanceof EnchantmentStorageMeta) {
        ((EnchantmentStorageMeta) itemMeta)
            .addStoredEnchant(EnchantmentSerializer.matchEnchantment(data[0]),
                Integer.parseInt(data[1]), true);
      } else {
        itemMeta
            .addEnchant(EnchantmentSerializer.matchEnchantment(data[0]), Integer.parseInt(data[1]),
                true);
      }
    }
  }
}
