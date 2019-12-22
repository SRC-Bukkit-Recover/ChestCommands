/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.chestcommands.serializer;

import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.config.yaml.PluginConfig;
import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.MenuData;
import com.gmail.filoghost.chestcommands.serializer.IconSerializer.Coords;
import com.gmail.filoghost.chestcommands.util.ClickType;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryType;

public class MenuSerializer {

  private MenuSerializer() {

  }

  public static ExtendedIconMenu loadMenu(PluginConfig config, String title, int slots,
      InventoryType inventoryType,
      ErrorLogger errorLogger) {
    ExtendedIconMenu iconMenu = new ExtendedIconMenu(title, slots, inventoryType,
        config.getFileName());

    for (String subSectionName : config.getKeys(false)) {
      if (subSectionName.equals("menu-settings")) {
        continue;
      }

      ConfigurationSection iconSection = config.getConfigurationSection(subSectionName);

      Icon icon = IconSerializer
          .loadIconFromSection(iconSection, subSectionName, config.getFileName(), errorLogger);

      if (subSectionName.equalsIgnoreCase("default-icon")) {
        iconMenu.fillIcon(icon);
        continue;
      }

      for (Coords coords : IconSerializer.loadCoordsFromSection(iconSection)) {
        if (!coords.isSetX() || !coords.isSetY()) {
          errorLogger.addError(
              "The icon \"" + subSectionName + "\" in the menu \"" + config.getFileName()
                  + " is missing POSITION-X and/or POSITION-Y.");
          continue;
        }

        iconMenu.setIcon(coords.getX(), coords.getY(), icon);
      }
    }

    return iconMenu;
  }

  /**
   * Reads all the settings of a menu. It will never return a null title, even if not set.
   */
  public static MenuData loadMenuData(PluginConfig config, ErrorLogger errorLogger) {

    String title = FormatUtils.addColors(config.getString(Nodes.MENU_NAME));
    int slots = 0;
    InventoryType inventoryType = InventoryType.CHEST;

    if (title == null) {
      errorLogger.addError("The menu \"" + config.getFileName() + "\" doesn't have a name set.");
      title = ChatColor.DARK_RED + "No title set";
    }

    if (title.length() > 32) {
      title = title.substring(0, 32);
    }

    if (config.isInt(Nodes.MENU_ROWS)) {
      slots = config.getInt(Nodes.MENU_ROWS) * 9;

      if (slots <= 0) {
        slots = 9;
      }

    } else if (config.isSet(Nodes.MENU_INV_TYPE)) {
      try {
        inventoryType = InventoryType.valueOf(config.getString(Nodes.MENU_INV_TYPE));
      } catch (IllegalArgumentException e) {
        errorLogger.addError("The menu \"" + config.getFileName()
            + "\" contains an illegal inventory type, it will be CHEST by default");
      }
      switch (inventoryType) {
        case FURNACE:
          slots = 3;
          break;
        case ENDER_CHEST:
        case CHEST:
          slots = 27;
          break;
        case HOPPER:
          slots = 5;
          break;
        case WORKBENCH:
          slots = 10;
          break;
        case DISPENSER:
        case DROPPER:
          slots = 9;
          break;
        default:
          errorLogger.addError("The menu \"" + config.getFileName()
              + "\"'s inventory type is not supported, it will be CHEST by default");
      }
    } else {
      slots = 6 * 9; // Defaults to 6 rows
      errorLogger.addError("The menu \"" + config.getFileName()
          + "\" doesn't have a the number of rows set, it will have 6 rows by default.");
    }

    MenuData menuData = new MenuData(title, slots, inventoryType);

    if (config.isSet(Nodes.MENU_COMMAND)) {
      menuData.setCommands(config.getString(Nodes.MENU_COMMAND).replace(" ", "").split(";"));
    }

    if (config.isSet(Nodes.OPEN_ACTION)) {
      menuData.setOpenActions(CommandSerializer.readCommands(config.getString(Nodes.OPEN_ACTION)));
    }

    if (config.isSet(Nodes.CLOSE_ACTION)) {
      menuData
          .setCloseActions(CommandSerializer.readCommands(config.getString(Nodes.CLOSE_ACTION)));
    }

    if (config.isSet(Nodes.OPEN_ITEM_MATERIAL)) {
      try {
        ItemStackReader itemReader = new ItemStackReader(config.getString(Nodes.OPEN_ITEM_MATERIAL),
            false);
        menuData.setBoundMaterial(itemReader.getMaterial());

        if (itemReader.hasExplicitDataValue()) {
          menuData.setBoundDataValue(itemReader.getDataValue());
        }
      } catch (FormatException e) {
        errorLogger.addError("The item \"" + config.getString(Nodes.OPEN_ITEM_MATERIAL)
            + "\" used to open the menu \"" + config.getFileName() + "\" is invalid: " + e
            .getMessage());
      }

      boolean leftClick = config.getBoolean(Nodes.OPEN_ITEM_LEFT_CLICK);
      boolean rightClick = config.getBoolean(Nodes.OPEN_ITEM_RIGHT_CLICK);

      if (leftClick || rightClick) {
        menuData.setClickType(ClickType.fromOptions(leftClick, rightClick));
      }
    }

    if (config.isSet(Nodes.AUTO_REFRESH)) {
      int ticksToRefresh = (int) (config.getDouble(Nodes.AUTO_REFRESH) * 20.0);
      if (ticksToRefresh < 1) {
        ticksToRefresh = 1;
      }
      menuData.setRefreshTicks(ticksToRefresh);
    }

    return menuData;
  }

  private static class Nodes {

    static final String MENU_NAME = "menu-settings.name";
    static final String MENU_ROWS = "menu-settings.rows";
    static final String MENU_INV_TYPE = "menu-settings.inventory-type";
    static final String MENU_COMMAND = "menu-settings.command";

    static final String OPEN_ACTION = "menu-settings.open-action";
    static final String CLOSE_ACTION = "menu-settings.close-action";

    static final String OPEN_ITEM_MATERIAL = "menu-settings.open-with-item.id";
    static final String OPEN_ITEM_LEFT_CLICK = "menu-settings.open-with-item.left-click";
    static final String OPEN_ITEM_RIGHT_CLICK = "menu-settings.open-with-item.right-click";

    static final String AUTO_REFRESH = "menu-settings.auto-refresh";
  }

}
