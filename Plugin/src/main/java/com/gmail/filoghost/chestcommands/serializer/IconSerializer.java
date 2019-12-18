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
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.api.event.IconCreateEvent;
import com.gmail.filoghost.chestcommands.config.AsciiPlaceholders;
import com.gmail.filoghost.chestcommands.exception.FormatException;
import com.gmail.filoghost.chestcommands.internal.CommandsClickHandler;
import com.gmail.filoghost.chestcommands.internal.icon.ExtendedIcon;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.util.Validate;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import com.gmail.filoghost.chestcommands.util.nbt.parser.MojangsonParseException;
import com.gmail.filoghost.chestcommands.util.nbt.parser.MojangsonParser;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;

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

    boolean closeOnClick = !section.getBoolean(Nodes.KEEP_OPEN);
    icon.setCloseOnClick(closeOnClick);

    CommandsClickHandler clickHandler = new CommandsClickHandler(closeOnClick);
    if (section.isConfigurationSection(Nodes.COMMAND)) {
      // PER CLICK TYPE
      for (ClickType type : ClickType.values()) {
        String subsection = Nodes.COMMAND + "." + type.name();
        if (section.isSet(subsection)) {
          List<IconCommand> commands;

          if (section.isList(subsection)) {
            commands = Utils.newArrayList();

            for (String commandString : section.getStringList(subsection)) {
              if (commandString.isEmpty()) {
                continue;
              }
              commands.add(CommandSerializer.matchCommand(commandString));
            }

          } else {
            commands = CommandSerializer.readCommands(section.getString(subsection));
          }

          clickHandler.setCommands(commands, type);
        }
      }

      // DEFAULT
      if (section.isSet(Nodes.COMMAND_DEFAULT)) {
        List<IconCommand> commands;

        if (section.isList(Nodes.COMMAND_DEFAULT)) {
          commands = Utils.newArrayList();

          for (String commandString : section.getStringList(Nodes.COMMAND_DEFAULT)) {
            if (commandString.isEmpty()) {
              continue;
            }
            commands.add(CommandSerializer.matchCommand(commandString));
          }

        } else {
          commands = CommandSerializer.readCommands(section.getString(Nodes.COMMAND_DEFAULT));
        }

        clickHandler.setDefaultCommands(commands);
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

      clickHandler.setDefaultCommands(commands);
    }
    icon.setClickHandler(clickHandler);

    if (section.isConfigurationSection(Nodes.VIEW_REQUIREMENT)) {
      icon.getRequirements().addViewRequirements(RequirementSerializer
          .loadRequirementsFromSection(section.getConfigurationSection(Nodes.VIEW_REQUIREMENT),
              iconName, menuFileName, errorLogger));
    }
    if (section.isConfigurationSection(Nodes.CLICK_REQUIREMENT)) {
      for (ClickType type : ClickType.values()) {
        String subsection = Nodes.CLICK_REQUIREMENT + "." + type;
        if (section.isConfigurationSection(subsection)) {
          icon.getRequirements().addClickRequirements(RequirementSerializer
              .loadRequirementsFromSection(section.getConfigurationSection(subsection),
                  iconName, menuFileName, errorLogger), type);
        }
      }
      icon.getRequirements().addDefaultClickRequirements(RequirementSerializer
          .loadRequirementsFromSection(section.getConfigurationSection(Nodes.CLICK_REQUIREMENT),
              iconName, menuFileName, errorLogger));
      if (section.isConfigurationSection(Nodes.CLICK_REQUIREMENT_DEFAULT)) {
        icon.getRequirements().addDefaultClickRequirements(RequirementSerializer
            .loadRequirementsFromSection(
                section.getConfigurationSection(Nodes.CLICK_REQUIREMENT_DEFAULT),
                iconName, menuFileName, errorLogger));
      }
    }

    if (section.isConfigurationSection(Nodes.COOLDOWN)) {
      // PER CLICK TYPE
      for (ClickType type : ClickType.values()) {
        String subsection = Nodes.COOLDOWN + "." + type.name();
        if (section.isSet(subsection)) {
          long cooldown = (long) (section.getDouble(subsection) * 1000);
          icon.getCooldown().setTime(cooldown, type);
        }
      }
      // DEFAULT
      if (section.isSet(Nodes.COOLDOWN_DEFAULT)) {
        long cooldown = (long) (section.getDouble(Nodes.COMMAND_DEFAULT) * 1000);
        icon.getCooldown().setDefaultTime(cooldown);
      }
    } else if (section.isSet(Nodes.COOLDOWN)) {
      long cooldown = (long) (section.getDouble(Nodes.COOLDOWN) * 1000);
      icon.getCooldown().setDefaultTime(cooldown);
    }
    icon.getCooldown()
        .setCooldownMessage(FormatUtils.addColors(section.getString(Nodes.COOLDOWN_MESSAGE)));

    // Call the event for further setting
    Bukkit.getPluginManager()
        .callEvent(new IconCreateEvent(icon, iconName, menuFileName, section, errorLogger));

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

    private final Integer x;
    private final Integer y;

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
    static final String COMMAND_DEFAULT = "COMMAND.DEFAULT";
    static final String KEEP_OPEN = "KEEP-OPEN";
    static final String SLOT = "SLOT";
    static final String POSITION_X = "POSITION-X";
    static final String POSITION_Y = "POSITION-Y";
    static final String FIREWORK = "FIREWORK";
    static final String COOLDOWN = "COOLDOWN";
    static final String COOLDOWN_DEFAULT = "COOLDOWN.DEFAULT";
    static final String COOLDOWN_MESSAGE = "COOLDOWN-MESSAGE";
    static final String VIEW_REQUIREMENT = "VIEW-REQUIREMENT";
    static final String CLICK_REQUIREMENT = "CLICK-REQUIREMENT";
    static final String CLICK_REQUIREMENT_DEFAULT = "CLICK-REQUIREMENT.DEFAULT";
  }

}
