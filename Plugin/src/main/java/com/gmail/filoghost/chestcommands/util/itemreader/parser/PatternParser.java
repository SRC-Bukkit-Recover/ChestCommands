package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import java.util.Arrays;
import org.bukkit.block.Banner;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class PatternParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    ItemMeta itemMeta = reader.getItemMeta();
    if (reader.getMaterial().name().contains("SHIELD")) {
      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      Banner banner = (Banner) blockStateMeta.getBlockState();
      banner.setPatterns(ItemUtils.parseBannerPatternList(
          Arrays.asList(value.replace("|", ":").split(" "))));
      banner.update();
      blockStateMeta.setBlockState(banner);
    } else if (itemMeta instanceof BannerMeta) {
      ((BannerMeta) itemMeta).setPatterns(ItemUtils.parseBannerPatternList(
          Arrays.asList(value.replace("|", ":").split(" "))));
    }
  }
}
