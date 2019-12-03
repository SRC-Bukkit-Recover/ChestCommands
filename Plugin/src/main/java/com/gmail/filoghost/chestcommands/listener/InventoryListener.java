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
package com.gmail.filoghost.chestcommands.listener;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.api.IconMenu;
import com.gmail.filoghost.chestcommands.internal.BoundItem;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;
import com.gmail.filoghost.chestcommands.task.ExecuteCommandsTask;
import com.gmail.filoghost.chestcommands.task.RefreshMenusTask;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

  private static Map<Player, Long> antiClickSpam = Utils.newHashMap();
  private static Map<Player, RefreshMenusTask> refreshMenusTaskMap = Utils.newHashMap();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent event) {
    if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
      for (BoundItem boundItem : ChestCommands.getBoundItems()) {
        if (boundItem.isValidTrigger(event.getItem(), event.getAction())) {
          if (event.getPlayer().hasPermission(boundItem.getMenu().getPermission())) {
            boundItem.getMenu().open(event.getPlayer());
          } else {
            boundItem.getMenu().sendNoPermissionMessage(event.getPlayer());
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryOpen(InventoryOpenEvent event) {
    Player player = (Player) event.getPlayer();

    Inventory inventory = event.getInventory();
    if (inventory.getHolder() instanceof MenuInventoryHolder) {
      MenuInventoryHolder menuHolder = (MenuInventoryHolder) inventory.getHolder();

      if (menuHolder.getIconMenu() instanceof ExtendedIconMenu) {
        ExtendedIconMenu extMenu = (ExtendedIconMenu) menuHolder.getIconMenu();

        if (extMenu.getRefreshTicks() > 0) {
          refreshMenusTaskMap.put(player, new RefreshMenusTask(player, extMenu));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();

    Inventory inventory = event.getInventory();
    if (inventory.getHolder() instanceof MenuInventoryHolder) {
      MenuInventoryHolder menuHolder = (MenuInventoryHolder) inventory.getHolder();

      if (menuHolder.getIconMenu() instanceof ExtendedIconMenu) {
        ExtendedIconMenu extMenu = (ExtendedIconMenu) menuHolder.getIconMenu();

        if (refreshMenusTaskMap.containsKey(player) && refreshMenusTaskMap.get(player).getExtMenu()
            .equals(extMenu)) {
          refreshMenusTaskMap.remove(player).getTask().cancel();
        }

        // RUN CLOSE ACTIONS
        List<IconCommand> closeActions = extMenu.getCloseActions();
        if (closeActions != null) {
          TaskChain<?> taskChain = ChestCommands.getTaskChainFactory().newChain();

          for (IconCommand closeAction : closeActions) {
            closeAction.addToTaskChain(player, taskChain);
          }

          taskChain.execute();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getInventory().getHolder() instanceof MenuInventoryHolder) {

      event.setCancelled(
          true); // First thing to do, if an exception is thrown at least the player doesn't take the item

      IconMenu menu = ((MenuInventoryHolder) event.getInventory().getHolder()).getIconMenu();
      int slot = event.getRawSlot();

      if (slot >= 0 && slot < menu.getSize()) {

        Icon icon = menu.getIconRaw(slot);

        if (icon != null && event.getInventory().getItem(slot) != null) {
          Player clicker = (Player) event.getWhoClicked();

          Long cooldownUntil = antiClickSpam.get(clicker);
          long now = System.currentTimeMillis();
          int minDelay = ChestCommands.getSettings().anti_click_spam_delay;

          if (minDelay > 0) {
            if (cooldownUntil != null && cooldownUntil > now) {
              return;
            } else {
              antiClickSpam.put(clicker, now + minDelay);
            }
          }

          // Get click type
          ClickType clickType = event.getClick();

          // Closes the inventory and executes commands AFTER the event
          Bukkit.getScheduler().scheduleSyncDelayedTask(ChestCommands.getInstance(),
              new ExecuteCommandsTask(clicker, icon, clickType));
        }
      }
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    antiClickSpam.remove(event.getPlayer());
  }

}
