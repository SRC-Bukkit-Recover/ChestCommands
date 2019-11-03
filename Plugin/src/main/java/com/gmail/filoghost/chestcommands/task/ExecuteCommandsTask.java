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
package com.gmail.filoghost.chestcommands.task;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.api.IconMenu;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.icon.IconCommand;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ExecuteCommandsTask implements Runnable {

  private Player player;
  private Icon icon;
  private IconMenu menu;
  private ClickType clickType;


  public ExecuteCommandsTask(Player player, IconMenu menu, Icon icon, ClickType clickType) {
    this.player = player;
    this.icon = icon;
    this.clickType = clickType;
    this.menu = menu;
  }


  @Override
  public void run() {
    boolean close = icon.onClick(player, clickType);

    if (close) {
      player.closeInventory();

      // RUN CLOSE ACTIONS
      if (menu instanceof ExtendedIconMenu) {
        List<IconCommand> closeActions = ((ExtendedIconMenu) menu).getCloseActions();
        if (closeActions != null) {
          TaskChain taskChain = ChestCommands.getTaskChainFactory().newChain();

          for (IconCommand closeAction : closeActions) {
            closeAction.execute(player, taskChain);
          }

          taskChain.execute();
        }
      }
    }
  }


}
