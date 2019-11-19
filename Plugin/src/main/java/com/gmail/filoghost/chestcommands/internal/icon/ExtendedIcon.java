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
package com.gmail.filoghost.chestcommands.internal.icon;

import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.internal.Cooldown;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;
import com.gmail.filoghost.chestcommands.internal.requirement.Requirements;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ExtendedIcon extends Icon {

  private Requirements requirements = new Requirements();
  private Cooldown cooldown = new Cooldown();

  public ExtendedIcon() {
    super();
  }

  @Override
  public String calculateName(Player pov) {
    return super.calculateName(pov);
  }

  @Override
  public List<String> calculateLore(Player pov) {
    return super.calculateLore(pov);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean onClick(Player player, ClickType clickType) {

    // Check all the requirements

    if (cooldown.isCooldown(player, clickType)) {
      return closeOnClick;
    }

    if (!requirements.check(player, clickType)) {
      return closeOnClick;
    }

    // Take things that are required

    requirements.take(player, clickType);

    InventoryView view = player.getOpenInventory();
    if (view != null) {
      Inventory topInventory = view.getTopInventory();
      if (topInventory.getHolder() instanceof MenuInventoryHolder) {
        MenuInventoryHolder menuHolder = (MenuInventoryHolder) topInventory.getHolder();

        if (menuHolder.getIconMenu() instanceof ExtendedIconMenu) {
          ((ExtendedIconMenu) menuHolder.getIconMenu()).refresh(player, topInventory);
        }
      }
    }

    return super.onClick(player, clickType);
  }

  public Cooldown getCooldown() {
    return cooldown;
  }

  public Requirements getRequirements() {
    return requirements;
  }
}
