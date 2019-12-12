package com.gmail.filoghost.chestcommands.util.itemreader;

import com.gmail.filoghost.chestcommands.exception.FormatException;

public interface ItemParser {
  void parse(ItemStackReader reader, String value) throws FormatException;
}
