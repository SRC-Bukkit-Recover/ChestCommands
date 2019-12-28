package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.internal.RequiredItem;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.MaterialsRegistry;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.VersionUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ItemIconRequirement extends IconRequirement {

  private List<RequiredItem> requiredItems = Utils.newArrayList();

  public ItemIconRequirement() {
    super(true, ValueType.STRING);
  }

  @Override
  public boolean check(Player player) {
    if (!requiredItems.isEmpty()) {

      boolean notHasItem = false;

      for (RequiredItem requiredItem : requiredItems) {

        if (!requiredItem.hasItem(player)) {
          notHasItem = true;
          String message =
              failMessage != null ? failMessage : ChestCommands.getLang().no_required_item;
          message = message
              .replace("{item}",
                  (requiredItem.hasItemMeta() && requiredItem.getItemMeta().hasDisplayName())
                      ? requiredItem.getItemMeta().getDisplayName()
                      : MaterialsRegistry.formatMaterial(requiredItem.getMaterial()))
              .replace("{amount}", Integer.toString(requiredItem.getAmount()))
              .replace("{datavalue}", requiredItem.hasRestrictiveDataValue() ? Short
                  .toString(requiredItem.getDataValue()) : ChestCommands.getLang().any);
          if (VersionUtils.isSpigot() && ChestCommands
              .getSettings().use_hover_event_on_required_item_message) {
            String itemJson = ItemUtils.convertItemStackToJson(requiredItem.createItemStack());

            BaseComponent[] hoverEventComponents = new BaseComponent[]{new TextComponent(itemJson)};

            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

            TextComponent component = new TextComponent(message);
            component.setHoverEvent(event);

            player.spigot().sendMessage(component);
          } else {
            player.sendMessage(message);
          }
        }

      }

      return !notHasItem;

    }
    return true;
  }

  @Override
  public void take(Player player) {
    if (!requiredItems.isEmpty()) {
      requiredItems.forEach(requiredItem -> requiredItem.takeItem(player));
    }
  }

  @Override
  public void setValues(List<String> values) {
    List<RequiredItem> requiredItemList = new ArrayList<>();
    for (String requiredItemText : values) {
      try {
        ItemStackReader itemReader = new ItemStackReader(requiredItemText, true);
        RequiredItem requiredItem = new RequiredItem(itemReader);

        requiredItemList.add(requiredItem);
      } catch (FormatException e) {
        ChestCommands.getInstance().getLogger()
            .log(Level.WARNING, "There is an invalid item requirement: " + values);
      }
    }
    this.requiredItems = requiredItemList;
  }
}
