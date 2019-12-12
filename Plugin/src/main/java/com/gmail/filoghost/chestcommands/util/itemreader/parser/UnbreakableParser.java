package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;

public class UnbreakableParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) {
    reader.setUnbreakable();
  }
}
