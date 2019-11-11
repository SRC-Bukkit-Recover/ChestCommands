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
import com.gmail.filoghost.chestcommands.config.AsciiPlaceholders;
import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.internal.CommandsClickHandler;
import com.gmail.filoghost.chestcommands.internal.RequiredItem;
import com.gmail.filoghost.chestcommands.internal.icon.ExtendedIcon;
import com.gmail.filoghost.chestcommands.internal.icon.IconCommand;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.ItemStackReader;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.Validate;
import com.gmail.filoghost.chestcommands.util.nbt.parser.MojangsonParseException;
import com.gmail.filoghost.chestcommands.util.nbt.parser.MojangsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;

public class IconSerializer {

  private IconSerializer() {

  }

  public static Icon loadIconFromSection(ConfigurationSection section, String iconName,
      String menuFileName, ErrorLogger errorLogger) {
    Validate.notNull(section, "ConfigurationSection cannot be null");

    // The icon is valid even without a Material
    ExtendedIcon icon = new ExtendedIcon();

    if (section.isSet(Nodes.ID)) {
      try {
        ItemStackReader itemReader = new ItemStackReader(section.getString(Nodes.ID), true);
        icon.setMaterial(itemReader.getMaterial());
        icon.setDataValue(itemReader.getDataValue());
        icon.setAmount(itemReader.getAmount());
      } catch (FormatException e) {
        errorLogger.addError(
            "The icon \"" + iconName + "\" in the menu \"" + menuFileName + "\" has an invalid ID: "
                + e.getMessage());
      }
    }

    if (section.isSet(Nodes.AMOUNT)) {
      icon.setAmount(section.getInt(Nodes.AMOUNT));
    }

    if (section.isSet(Nodes.DURABILITY)) {
      icon.setDataValue((short) section.getInt(Nodes.DURABILITY));
    } else if (section.isSet(Nodes.DATA_VALUE)) { // Alias
      icon.setDataValue((short) section.getInt(Nodes.DATA_VALUE));
    }

    if (section.isSet(Nodes.NBT_DATA)) {
      String nbtData = section.getString(Nodes.NBT_DATA);
      try {
        // Check that NBT has valid syntax before applying it to the icon
        MojangsonParser.parse(nbtData);
        icon.setNBTData(nbtData);
      } catch (MojangsonParseException e) {
        errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
            + "\" has an invalid NBT-DATA: " + e.getMessage());
      }
    }

    icon.setName(AsciiPlaceholders
        .placeholdersToSymbols(FormatUtils.colorizeName(section.getString(Nodes.NAME))));
    icon.setLore(AsciiPlaceholders
        .placeholdersToSymbols(FormatUtils.colorizeLore(section.getStringList(Nodes.LORE))));

    if (section.isSet(Nodes.ENCHANT)) {
      icon.setEnchantments(EnchantmentSerializer
          .loadEnchantments(section.getString(Nodes.ENCHANT), iconName, menuFileName, errorLogger));
    }

    if (section.isSet(Nodes.COLOR)) {
      try {
        icon.setColor(ItemUtils.parseColor(section.getString(Nodes.COLOR)));
      } catch (FormatException e) {
        errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
            + "\" has an invalid COLOR: " + e.getMessage());
      }
    }

    icon.setSkullOwner(section.getString(Nodes.SKULL_OWNER));

    if (section.isSet(Nodes.BANNER_COLOR)) {
      try {
        icon.setBannerColor(ItemUtils.parseDyeColor(section.getString(Nodes.BANNER_COLOR)));
      } catch (FormatException e) {
        errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
            + "\" has an invalid BASE-COLOUR: " + e.getMessage());
      }
    }

    if (section.isSet(Nodes.BANNER_PATTERNS)) {
      try {
        icon.setBannerPatterns(
            ItemUtils.parseBannerPatternList(section.getStringList(Nodes.BANNER_PATTERNS)));
      } catch (FormatException e) {
        errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
            + "\" has an invalid PATTERN-LIST: " + e.getMessage());
      }
    }

    List<FireworkEffect> fireworkEffects = new ArrayList<>();
    if (section.isSet(Nodes.FIREWORK)) {
      try {
        if (section.isList(Nodes.FIREWORK)) {
          for (String firework : section.getStringList(Nodes.FIREWORK)) {
            fireworkEffects.add(ItemUtils.parseFireworkEffect(firework));
          }
        } else {
          for (String firework : section.getString(Nodes.FIREWORK).split(" ")) {
            fireworkEffects.add(ItemUtils.parseFireworkEffect(firework));
          }
        }
        icon.setFireworkEffects(fireworkEffects);
      } catch (FormatException e) {
        errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
            + "\" has an invalid FIREWORK: " + e.getMessage());
      }
    }

    icon.setPermission(section.getString(Nodes.PERMISSION));
    icon.setPermissionMessage(FormatUtils.addColors(section.getString(Nodes.PERMISSION_MESSAGE)));
    icon.setViewPermission(section.getString(Nodes.VIEW_PERMISSION));

    icon.setViewRequirement(section.getString(Nodes.VIEW_REQUIREMENT));
    icon.setClickRequirement(section.getString(Nodes.CLICK_REQUIREMENT));
    icon.setClickRequirementMessage(
        FormatUtils.addColors(section.getString(Nodes.CLICK_REQUIREMENT_MESSAGE)));

    boolean closeOnClick = !section.getBoolean(Nodes.KEEP_OPEN);
    icon.setCloseOnClick(closeOnClick);

    if (section.isConfigurationSection(Nodes.COMMAND)) {
      // LEFT COMMAND
      if (section.isSet(Nodes.COMMAND_LEFT)) {

        List<IconCommand> commands;

        if (section.isList(Nodes.COMMAND_LEFT)) {
          commands = Utils.newArrayList();

          for (String commandString : section.getStringList(Nodes.COMMAND_LEFT)) {
            if (commandString.isEmpty()) {
              continue;
            }
            commands.add(CommandSerializer.matchCommand(commandString));
          }

        } else {
          commands = CommandSerializer.readCommands(section.getString(Nodes.COMMAND_LEFT));
        }

        icon.setClickLeftHandler(new CommandsClickHandler(commands, closeOnClick));
      }
      // RIGHT COMMAND
      if (section.isSet(Nodes.COMMAND_RIGHT)) {

        List<IconCommand> commands;

        if (section.isList(Nodes.COMMAND_RIGHT)) {
          commands = Utils.newArrayList();

          for (String commandString : section.getStringList(Nodes.COMMAND_RIGHT)) {
            if (commandString.isEmpty()) {
              continue;
            }
            commands.add(CommandSerializer.matchCommand(commandString));
          }

        } else {
          commands = CommandSerializer.readCommands(section.getString(Nodes.COMMAND_RIGHT));
        }

        icon.setClickRightHandler(new CommandsClickHandler(commands, closeOnClick));
      }
      // MIDDLE COMMAND
      if (section.isSet(Nodes.COMMAND_MIDDLE)) {

        List<IconCommand> commands;

        if (section.isList(Nodes.COMMAND_MIDDLE)) {
          commands = Utils.newArrayList();

          for (String commandString : section.getStringList(Nodes.COMMAND_MIDDLE)) {
            if (commandString.isEmpty()) {
              continue;
            }
            commands.add(CommandSerializer.matchCommand(commandString));
          }

        } else {
          commands = CommandSerializer.readCommands(section.getString(Nodes.COMMAND_MIDDLE));
        }

        icon.setClickMiddleHandler(new CommandsClickHandler(commands, closeOnClick));
      }
    } else if (section.isSet(Nodes.COMMAND)) {

      List<IconCommand> commands;

      if (section.isList(Nodes.COMMAND)) {
        commands = Utils.newArrayList();

        for (String commandString : section.getStringList(Nodes.COMMAND)) {
          if (commandString.isEmpty()) {
            continue;
          }
          commands.add(CommandSerializer.matchCommand(commandString));
        }

      } else {
        commands = CommandSerializer.readCommands(section.getString(Nodes.COMMAND));
      }

      icon.setClickHandler(new CommandsClickHandler(commands, closeOnClick));
    }

    icon.setMoneyPrice(section.getString(Nodes.PRICE, "0"));
    icon.setPlayerPointsPrice(section.getString(Nodes.POINTS, "0"));
    icon.setTokenManagerPrice(section.getString(Nodes.TOKENS, "0"));
    icon.setExpLevelsPrice(section.getString(Nodes.EXP_LEVELS, "0"));

    if (section.isConfigurationSection(Nodes.COOLDOWN)) {
      // LEFT CLICK COOLDOWN
      if (section.isSet(Nodes.COOLDOWN_LEFT)) {
        long cooldown = (long) (section.getDouble(Nodes.COOLDOWN_LEFT) * 1000);
        icon.setLeftCooldown(cooldown);
      }
      // RIGHT CLICK COOLDOWN
      if (section.isSet(Nodes.COOLDOWN_RIGHT)) {
        long cooldown = (long) (section.getDouble(Nodes.COOLDOWN_RIGHT) * 1000);
        icon.setRightCooldown(cooldown);
      }
      // MIDDLE CLICK COOLDOWN
      if (section.isSet(Nodes.COOLDOWN_MIDDLE)) {
        long cooldown = (long) (section.getDouble(Nodes.COOLDOWN_MIDDLE) * 1000);
        icon.setMiddleCooldown(cooldown);
      }
    } else if (section.isSet(Nodes.COOLDOWN)) {
      long cooldown = (long) (section.getDouble(Nodes.COOLDOWN) * 1000);
      icon.setLeftCooldown(cooldown);
      icon.setRightCooldown(cooldown);
      icon.setMiddleCooldown(cooldown);
      icon.setCooldownAll(true);
    }
    icon.setCooldownMessage(FormatUtils.addColors(section.getString(Nodes.COOLDOWN_MESSAGE)));

    if (section.isConfigurationSection(Nodes.REQUIRED_ITEM)) {
      // LEFT REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_LEFT)) {
        List<String> requiredItemsStrings;
        if (section.isList(Nodes.REQUIRED_ITEM_LEFT)) {
          requiredItemsStrings = section.getStringList(Nodes.REQUIRED_ITEM_LEFT);
        } else if (section.isString(Nodes.REQUIRED_ITEM_LEFT) && section
            .getString(Nodes.REQUIRED_ITEM_LEFT)
            .contains(";")) {
          requiredItemsStrings = Arrays
              .asList(section.getString(Nodes.REQUIRED_ITEM_LEFT).split(";"));
        } else {
          requiredItemsStrings = Collections
              .singletonList(section.getString(Nodes.REQUIRED_ITEM_LEFT));
        }
        // ASCII Translate
        requiredItemsStrings = AsciiPlaceholders.placeholdersToSymbols(requiredItemsStrings);

        List<RequiredItem> requiredItems = new ArrayList<>();
        for (String requiredItemText : requiredItemsStrings) {
          try {
            ItemStackReader itemReader = new ItemStackReader(requiredItemText, true);
            RequiredItem requiredItem = new RequiredItem(itemReader);

            requiredItems.add(requiredItem);
          } catch (FormatException e) {
            errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
                + "\" has an invalid REQUIRED-ITEM.LEFT: " + e.getMessage());
          }
        }
        icon.setLeftRequiredItems(requiredItems);
      }
      // RIGHT REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_RIGHT)) {
        List<String> requiredItemsStrings;
        if (section.isList(Nodes.REQUIRED_ITEM_RIGHT)) {
          requiredItemsStrings = section.getStringList(Nodes.REQUIRED_ITEM_RIGHT);
        } else if (section.isString(Nodes.REQUIRED_ITEM_RIGHT) && section
            .getString(Nodes.REQUIRED_ITEM_RIGHT)
            .contains(";")) {
          requiredItemsStrings = Arrays
              .asList(section.getString(Nodes.REQUIRED_ITEM_RIGHT).split(";"));
        } else {
          requiredItemsStrings = Collections
              .singletonList(section.getString(Nodes.REQUIRED_ITEM_RIGHT));
        }

        List<RequiredItem> requiredItems = new ArrayList<>();
        for (String requiredItemText : requiredItemsStrings) {
          try {
            ItemStackReader itemReader = new ItemStackReader(requiredItemText, true);
            RequiredItem requiredItem = new RequiredItem(itemReader);

            requiredItems.add(requiredItem);
          } catch (FormatException e) {
            errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
                + "\" has an invalid REQUIRED-ITEM.RIGHT: " + e.getMessage());
          }
        }
        icon.setRightRequiredItems(requiredItems);
      }
      // MIDDLE REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_MIDDLE)) {
        List<String> requiredItemsStrings;
        if (section.isList(Nodes.REQUIRED_ITEM_MIDDLE)) {
          requiredItemsStrings = section.getStringList(Nodes.REQUIRED_ITEM_MIDDLE);
        } else if (section.isString(Nodes.REQUIRED_ITEM_MIDDLE) && section
            .getString(Nodes.REQUIRED_ITEM_MIDDLE)
            .contains(";")) {
          requiredItemsStrings = Arrays
              .asList(section.getString(Nodes.REQUIRED_ITEM_MIDDLE).split(";"));
        } else {
          requiredItemsStrings = Collections
              .singletonList(section.getString(Nodes.REQUIRED_ITEM_MIDDLE));
        }

        List<RequiredItem> requiredItems = new ArrayList<>();
        for (String requiredItemText : requiredItemsStrings) {
          try {
            ItemStackReader itemReader = new ItemStackReader(requiredItemText, true);
            RequiredItem requiredItem = new RequiredItem(itemReader);

            requiredItems.add(requiredItem);
          } catch (FormatException e) {
            errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
                + "\" has an invalid REQUIRED-ITEM.MIDDLE: " + e.getMessage());
          }
        }
        icon.setMiddleRequiredItems(requiredItems);
      }
    } else if (section.isSet(Nodes.REQUIRED_ITEM)) {
      List<String> requiredItemsStrings;
      if (section.isList(Nodes.REQUIRED_ITEM)) {
        requiredItemsStrings = section.getStringList(Nodes.REQUIRED_ITEM);
      } else if (section.isString(Nodes.REQUIRED_ITEM) && section.getString(Nodes.REQUIRED_ITEM)
          .contains(";")) {
        requiredItemsStrings = Arrays.asList(section.getString(Nodes.REQUIRED_ITEM).split(";"));
      } else {
        requiredItemsStrings = Collections.singletonList(section.getString(Nodes.REQUIRED_ITEM));
      }

      List<RequiredItem> requiredItems = new ArrayList<>();
      for (String requiredItemText : requiredItemsStrings) {
        try {
          ItemStackReader itemReader = new ItemStackReader(requiredItemText, true);
          RequiredItem requiredItem = new RequiredItem(itemReader);

          requiredItems.add(requiredItem);
        } catch (FormatException e) {
          errorLogger.addError("The icon \"" + iconName + "\" in the menu \"" + menuFileName
              + "\" has an invalid REQUIRED-ITEM: " + e.getMessage());
        }
      }
      icon.setRequiredItems(requiredItems);
    }

    return icon;
  }

  public static List<Coords> loadCoordsFromSection(ConfigurationSection section) {
    Validate.notNull(section, "ConfigurationSection cannot be null");

    List<Coords> coords = Utils.newArrayList();

    if (section.isSet(Nodes.SLOT)) {
      if (section.isInt(Nodes.SLOT)) {
        int slot = section.getInt(Nodes.SLOT);
        coords.add(toCoords(slot));
      } else if (section.isList(Nodes.SLOT)) {
        for (String string : section.getStringList(Nodes.SLOT)) {
          coords.addAll(toCoords(string));
        }
      } else if (section.isString(Nodes.SLOT)) {
        coords.addAll(toCoords(section.getString(Nodes.SLOT)));
      }
    } else {
      Integer x = null;
      Integer y = null;

      if (section.isInt(Nodes.POSITION_X)) {
        x = section.getInt(Nodes.POSITION_X);
      }

      if (section.isInt(Nodes.POSITION_Y)) {
        y = section.getInt(Nodes.POSITION_Y);
      }

      coords.add(new Coords(x, y));
    }

    return coords;
  }

  private static Coords toCoords(int slot) {
    int x;
    int y;
    if (slot > 9) {
      x = slot;
      y = 1;
      while (x > 9) {
        y += 1;
        x -= 9;
      }
    } else {
      x = slot;
      y = 1;
    }
    return new Coords(x, y);
  }

  private static List<Coords> toCoords(String input) {
    List<Coords> coords = Utils.newArrayList();
    for (String string : input.split(",")) {
      if (Utils.isValidInteger(string)) {
        coords.add(toCoords(Integer.parseInt(string)));
      } else {
        String[] split = string.split("-", 2);
        if (Utils.isValidInteger(split[0]) && Utils.isValidInteger(split[1])) {
          int s1 = Integer.parseInt(split[0]);
          int s2 = Integer.parseInt(split[1]);
          int start = Math.min(s1, s2);
          int end = Math.max(s1, s2);
          for (int i = start; i <= end; i++) {
            coords.add(toCoords(i));
          }
        }
      }
    }
    return coords;
  }

  static class Coords {

    private Integer x;
    private Integer y;

    Coords(Integer x, Integer y) {
      this.x = x;
      this.y = y;
    }

    boolean isSetX() {
      return x != null;
    }

    boolean isSetY() {
      return y != null;
    }

    Integer getX() {
      return x;
    }

    Integer getY() {
      return y;
    }
  }

  private static class Nodes {

    static final String ID = "ID";
    static final String AMOUNT = "AMOUNT";
    static final String DATA_VALUE = "DATA-VALUE";
    static final String DURABILITY = "DURABILITY";
    static final String NBT_DATA = "NBT-DATA";
    static final String NAME = "NAME";
    static final String LORE = "LORE";
    static final String ENCHANT = "ENCHANTMENT";
    static final String COLOR = "COLOR";
    static final String SKULL_OWNER = "SKULL-OWNER";
    static final String BANNER_COLOR = "BANNER-COLOUR";
    static final String BANNER_PATTERNS = "BANNER-PATTERNS";
    static final String COMMAND = "COMMAND";
    static final String COMMAND_LEFT = "COMMAND.LEFT";
    static final String COMMAND_RIGHT = "COMMAND.RIGHT";
    static final String COMMAND_MIDDLE = "COMMAND.MIDDLE";
    static final String PRICE = "PRICE";
    static final String POINTS = "POINTS";
    static final String TOKENS = "TOKENS";
    static final String EXP_LEVELS = "LEVELS";
    static final String REQUIRED_ITEM = "REQUIRED-ITEM";
    static final String REQUIRED_ITEM_LEFT = "REQUIRED-ITEM.LEFT";
    static final String REQUIRED_ITEM_RIGHT = "REQUIRED-ITEM.RIGHT";
    static final String REQUIRED_ITEM_MIDDLE = "REQUIRED-ITEM.MIDDLE";
    static final String PERMISSION = "PERMISSION";
    static final String PERMISSION_MESSAGE = "PERMISSION-MESSAGE";
    static final String VIEW_PERMISSION = "VIEW-PERMISSION";
    static final String KEEP_OPEN = "KEEP-OPEN";
    static final String SLOT = "SLOT";
    static final String POSITION_X = "POSITION-X";
    static final String POSITION_Y = "POSITION-Y";
    static final String FIREWORK = "FIREWORK";
    static final String COOLDOWN = "COOLDOWN";
    static final String COOLDOWN_LEFT = "COOLDOWN.LEFT";
    static final String COOLDOWN_RIGHT = "COOLDOWN.RIGHT";
    static final String COOLDOWN_MIDDLE = "COOLDOWN.MIDDLE";
    static final String COOLDOWN_MESSAGE = "COOLDOWN-MESSAGE";
    static final String VIEW_REQUIREMENT = "VIEW-REQUIREMENT";
    static final String CLICK_REQUIREMENT = "CLICK-REQUIREMENT";
    static final String CLICK_REQUIREMENT_MESSAGE = "CLICK-REQUIREMENT-MESSAGE";
  }

}
