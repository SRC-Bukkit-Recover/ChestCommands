package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.block.Banner;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class BaseColorParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    ItemMeta meta = reader.getItemMeta();
    if (reader.getMaterial().name().contains("SHIELD")) {
      BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
      Banner banner = (Banner) blockStateMeta.getBlockState();
      banner.setBaseColor(ItemUtils.parseDyeColor(value));
      banner.update();
      blockStateMeta.setBlockState(banner);
    } else if (meta instanceof BannerMeta) {
      ((BannerMeta) meta).setBaseColor(ItemUtils.parseDyeColor(value));
    }
  }
}
