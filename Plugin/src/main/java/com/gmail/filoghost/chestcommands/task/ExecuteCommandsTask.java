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

import com.gmail.filoghost.chestcommands.api.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ExecuteCommandsTask implements Runnable {

  private final Player player;
  private final Icon icon;
  private final ClickType clickType;


  public ExecuteCommandsTask(Player player, Icon icon, ClickType clickType) {
    this.player = player;
    this.icon = icon;
    this.clickType = clickType;
  }


  @Override
  public void run() {
    boolean close = icon.onClick(player, clickType);

    if (close) {
      player.closeInventory();
    }
  }


}
