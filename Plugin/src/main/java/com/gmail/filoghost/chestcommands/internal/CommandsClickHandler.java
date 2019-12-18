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
package com.gmail.filoghost.chestcommands.internal;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.ClickHandler;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.OpenIconCommand;
import com.gmail.filoghost.chestcommands.internal.icon.command.RefreshIconCommand;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CommandsClickHandler implements ClickHandler {

  private final Map<ClickType, List<IconCommand>> commandsPerClickType = Utils.newHashMap();
  private List<IconCommand> defaultCommands = Utils.newArrayList();
  private final boolean closeOnClick;

  public CommandsClickHandler(boolean closeOnClick) {
    this.closeOnClick = closeOnClick;
  }

  @Override
  public boolean onClick(Player player, ClickType clickType) {
    List<IconCommand> commands = commandsPerClickType.getOrDefault(clickType, defaultCommands);
    boolean close = closeOnClick;
    if (!commands.isEmpty()) {
      TaskChain<?> taskChain = ChestCommands.getTaskChainFactory().newChain();

      for (IconCommand command : commands) {
        command.addToTaskChain(player, taskChain);
        if (command instanceof OpenIconCommand || command instanceof RefreshIconCommand) {
          // Fix GUI closing if KEEP-OPEN is not set, and a command should open another GUI
          close = false;
          break;
        }
      }

      taskChain.execute();
    }

    return close;
  }

  public void setCommands(List<IconCommand> commands, ClickType clickType) {
    commandsPerClickType.put(clickType, commands);
  }

  public void setDefaultCommands(List<IconCommand> commands) {
    this.defaultCommands = commands;
  }
}
