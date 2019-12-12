package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.inventory.ItemFlag;

public class FlagParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    for (String flag : value.split(" ")) {
      try {
        reader.getItemMeta().addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
      } catch (Exception e) {
        throw new FormatException("invalid item flags \"" + flag + "\"");
      }
    }
  }
}
