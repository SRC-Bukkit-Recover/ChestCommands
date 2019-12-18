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

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.BroadcastIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.CloseMenuCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.ConditionIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.ConsoleIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.DelayIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.DragonBarIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.GiveIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.GiveMoneyIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.GivePointsIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.GiveTokensIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.OpIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.OpenIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.PlayerIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.RefreshIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.ServerIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.SoundIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.TellIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.TitleIconCommand;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSerializer {

  private static final Map<Pattern, Class<? extends IconCommand>> commandTypesMap = Utils.newHashMap();

  static {
    register("console:", ConsoleIconCommand.class);
    register("op:", OpIconCommand.class);
    register("(open|menu):", OpenIconCommand.class);
    register("refresh:", RefreshIconCommand.class);
    register("server:?", ServerIconCommand.class); // The colon is optional
    register("tell:", TellIconCommand.class);
    register("broadcast:", BroadcastIconCommand.class);
    register("give:", GiveIconCommand.class);
    register("give-?money:", GiveMoneyIconCommand.class);
    register("give-?points:", GivePointsIconCommand.class);
    register("sound:", SoundIconCommand.class);
    register("dragon-?bar:", DragonBarIconCommand.class);
    register("condition:", ConditionIconCommand.class);
    register("give-?tokens:", GiveTokensIconCommand.class);
    register("delay:", DelayIconCommand.class);
    register("title:", TitleIconCommand.class);
    register("close:", CloseMenuCommand.class);
  }

  private CommandSerializer() {

  }

  public static void register(String regex, Class<? extends IconCommand> clazz) {
    Pattern pattern = Pattern
        .compile("^(?i)" + regex); // Case insensitive and only at the beginning
    commandTypesMap.put(pattern, clazz);
  }

  public static void checkClassConstructors(ErrorLogger errorLogger) {
    for (Class<? extends IconCommand> clazz : commandTypesMap.values()) {
      try {
        clazz.getDeclaredConstructor(String.class).newInstance("");
      } catch (Exception ex) {
        String className = clazz.getName().replace("Command", "");
        className = className.substring(className.lastIndexOf('.') + 1);
        errorLogger.addError(
            "Unable to register the \"" + className + "\" command type(" + ex.getClass().getName()
                + "), please inform the developer (filoghost). The plugin will still work, but all the \""
                + className + "\" commands will be treated as normal commands.");
      }
    }
  }

  public static List<IconCommand> readCommands(String input) {
    String separator = ChestCommands.getSettings().multiple_commands_separator;
    if (separator == null || separator.length() == 0) {
      separator = ";";
    }

    String[] split = input.split(Pattern.quote(separator));
    List<IconCommand> iconCommands = Utils.newArrayList();

    for (String command : split) {
      String trim = command.trim();

      if (trim.length() > 0) {
        iconCommands.add(matchCommand(trim));
      }
    }

    return iconCommands;
  }

  public static IconCommand matchCommand(String input) {

    for (Entry<Pattern, Class<? extends IconCommand>> entry : commandTypesMap.entrySet()) {
      Matcher matcher = entry.getKey().matcher(input);
      if (matcher.find()) {
        // Remove the command prefix and trim the spaces
        String cleanCommand = matcher.replaceFirst("").trim();

        try {
          return entry.getValue().getDeclaredConstructor(String.class).newInstance(cleanCommand);
        } catch (Exception e) {
          // Checked at startup
        }
      }
    }

    return new PlayerIconCommand(input); // Normal command, no match found
  }

}
