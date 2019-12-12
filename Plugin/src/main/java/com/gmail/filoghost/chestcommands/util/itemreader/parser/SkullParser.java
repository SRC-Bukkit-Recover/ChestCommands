package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.util.SkullUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;

public class SkullParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) {
    reader.setItemMeta(SkullUtils.parseSkull(reader.getItemMeta(), value));
  }
}
