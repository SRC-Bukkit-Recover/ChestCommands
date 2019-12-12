package com.gmail.filoghost.chestcommands.util.itemreader.parser;

import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemParser;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FireworkParser implements ItemParser {

  @Override
  public void parse(ItemStackReader reader, String value) throws FormatException {
    String[] split = value.split(" ");
    ItemMeta itemMeta = reader.getItemMeta();
    if (itemMeta instanceof FireworkMeta) {
      List<FireworkEffect> effects = new ArrayList<>();
      for (String firework : split) {
        effects.add(ItemUtils.parseFireworkEffect(firework));
      }
      ((FireworkMeta) itemMeta).addEffects(effects);
    } else if (itemMeta instanceof FireworkEffectMeta) {
      ((FireworkEffectMeta) itemMeta).setEffect(ItemUtils.parseFireworkEffect(split[0]));
    }
  }
}
