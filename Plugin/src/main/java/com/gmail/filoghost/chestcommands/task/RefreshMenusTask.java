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

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RefreshMenusTask extends BukkitRunnable {

  private final Player player;
  private final ExtendedIconMenu extMenu;
  private final BukkitTask task;

  public RefreshMenusTask(Player player, ExtendedIconMenu extMenu) {
    this.player = player;
    this.extMenu = extMenu;
    task = runTaskTimerAsynchronously(ChestCommands.getInstance(), extMenu.getRefreshTicks(),
        extMenu.getRefreshTicks());
  }

  @Override
  public void run() {
    InventoryView view = player.getOpenInventory();
    if (view == null) {
      cancel();
    }

    extMenu.refresh(player, player.getOpenInventory().getTopInventory());
    player.updateInventory();
  }

  public ExtendedIconMenu getExtMenu() {
    return extMenu;
  }

  public BukkitTask getTask() {
    return task;
  }
}
