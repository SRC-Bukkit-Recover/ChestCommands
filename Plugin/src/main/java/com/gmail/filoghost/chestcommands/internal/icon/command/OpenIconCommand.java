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
package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OpenIconCommand extends IconCommand {

  public OpenIconCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String target = hasVariables ? getParsedCommand(player) : command;
    final ExtendedIconMenu menu = ChestCommands.getFileNameToMenuMap().get(target.toLowerCase());
    if (menu == null) {
      player.sendMessage(ChatColor.RED + "Menu not found! Please inform the staff.");
      return;
    }

    taskChain.sync(() -> {
      if (player.hasPermission(menu.getPermission())) {
        menu.open(player);
      } else {
        menu.sendNoPermissionMessage(player);
      }
    });
  }

}
