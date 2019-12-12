package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    ItemMeta itemMeta = reader.getItemMeta();
    if (itemMeta instanceof PotionMeta) {
      for (String potion : value.split(" ")) {
        String[] data = potion.split("\\|");
        if (data.length != 3) {
          throw new FormatException("invalid potion format \"" + value + "\"");
        }
        if (!(Utils.isValidInteger(data[1]) || Utils.isValidInteger(data[2]))) {
          throw new FormatException("invalid integer \"" + value + "\"");
        }
        if (PotionEffectType.getByName(data[0]) == null) {
          throw new FormatException("invalid effect type \"" + value + "\"");
        }
        ((PotionMeta) itemMeta).addCustomEffect(
            new PotionEffect(PotionEffectType.getByName(data[0]), Integer.parseInt(data[1]),
                Integer.parseInt(data[2])), true);
      }
    }  }
}
