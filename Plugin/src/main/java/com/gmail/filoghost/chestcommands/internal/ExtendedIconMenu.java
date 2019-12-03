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
import com.gmail.filoghost.chestcommands.Permissions;
import com.gmail.filoghost.chestcommands.api.IconCommand;
import com.gmail.filoghost.chestcommands.api.IconMenu;
import com.gmail.filoghost.chestcommands.internal.icon.ExtendedIcon;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExtendedIconMenu extends IconMenu {

  private String fileName;
  private String permission;
  private List<IconCommand> openActions;
  private List<IconCommand> closeActions;

  private int refreshTicks;

  public ExtendedIconMenu(String title, int slots, InventoryType inventoryType, String fileName) {
    super(title, slots, inventoryType);
    this.fileName = fileName;
    this.permission = Permissions.OPEN_MENU_BASE + fileName;
  }

  public List<IconCommand> getOpenActions() {
    return openActions;
  }

  public void setOpenActions(List<IconCommand> openAction) {
    this.openActions = openAction;
  }

  public String getPermission() {
    return permission;
  }

  public String getFileName() {
    return fileName;
  }

  public int getRefreshTicks() {
    return refreshTicks;
  }

  public void setRefreshTicks(int refreshTicks) {
    this.refreshTicks = refreshTicks;
  }

  @Override
  public void open(Player player) {
    try {
      if (openActions != null) {
        TaskChain<?> taskChain = ChestCommands.getTaskChainFactory().newChain();

        for (IconCommand openAction : openActions) {
          openAction.execute(player, taskChain);
        }

        taskChain.execute();
      }

      Inventory inventory;
      if (inventoryType.equals(InventoryType.CHEST) && icons.length != 27) {
        inventory = Bukkit
            .createInventory(new MenuInventoryHolder(this), getSize(), getTitle(player));
      } else {
        inventory = Bukkit
            .createInventory(new MenuInventoryHolder(this), inventoryType, getTitle(player));
      }

      for (int i = 0; i < icons.length; i++) {
        if (icons[i] != null) {

          if (icons[i] instanceof ExtendedIcon) {
            if (((ExtendedIcon) icons[i]).getRequirements().canSee(player)) {
              ((ExtendedIcon) icons[i]).getRequirements().takeView(player);
            } else {
              continue;
            }
          }

          inventory.setItem(i, ItemUtils.hideAttributes(icons[i].createItemstack(player)));
        }
      }

      player.openInventory(inventory);
    } catch (Exception e) {
      ChestCommands.getInstance().getLogger()
          .log(Level.WARNING, "Unexpected error when opening the menu", e);
      player.sendMessage(ChatColor.RED
          + "An internal error occurred while opening the menu. The staff should check the console for errors.");
    }
  }

  public void refresh(Player player, Inventory inventory) {
    try {
      for (int i = 0; i < icons.length; i++) {
        if (icons[i] instanceof ExtendedIcon) {
          ExtendedIcon extIcon = (ExtendedIcon) icons[i];

          if (extIcon.getRequirements().hasViewRequirement() || extIcon
              .hasVariables()) {
            // Then we have to refresh it
            if (extIcon.getRequirements().canSee(player)) {

              if (inventory.getItem(i) == null) {
                ItemStack newItem = ItemUtils.hideAttributes(extIcon.createItemstack(player));
                inventory.setItem(i, newItem);
              } else {
                // Performance, only update name and lore
                ItemStack oldItem = ItemUtils.hideAttributes(inventory.getItem(i));
                ItemMeta meta = oldItem.getItemMeta();
                meta.setDisplayName(extIcon.calculateName(player));
                meta.setLore(extIcon.calculateLore(player));
                oldItem.setItemMeta(meta);
              }

            } else {
              inventory.setItem(i, null);
            }
          }
        }
      }
    } catch (Exception e) {
      ChestCommands.getInstance().getLogger()
          .log(Level.WARNING, "Unexpected error when refreshing the menu", e);
      player.sendMessage(ChatColor.RED
          + "An internal error occurred while refreshing the menu. The staff should check the console for errors.");
    }
  }

  public void sendNoPermissionMessage(CommandSender sender) {
    String noPermMessage = ChestCommands.getLang().no_open_permission;
    if (noPermMessage != null && !noPermMessage.isEmpty()) {
      sender.sendMessage(noPermMessage.replace("{permission}", this.permission));
    }
  }

  public List<IconCommand> getCloseActions() {
    return closeActions;
  }

  public void setCloseActions(List<IconCommand> closeActions) {
    this.closeActions = closeActions;
  }
}
