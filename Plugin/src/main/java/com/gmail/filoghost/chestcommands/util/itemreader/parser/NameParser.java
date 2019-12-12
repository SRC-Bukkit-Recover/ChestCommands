package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.config.AsciiPlaceholders;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.ChatColor;

public class NameParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) {
    reader.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&',
        AsciiPlaceholders
            .placeholdersToSymbols(value.replace("_", " "))));
  }
}
