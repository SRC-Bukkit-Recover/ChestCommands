package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.config.AsciiPlaceholders;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class LoreParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) {
    List<String> lore = new ArrayList<>();
    for (String line : value.split(" ")) {
      lore.add(ChatColor.translateAlternateColorCodes('&',
          AsciiPlaceholders.placeholdersToSymbols(line.replace("_", " "))));
    }
    reader.getItemMeta().setLore(lore);
  }
}
