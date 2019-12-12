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

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.util.Validate;
import com.gmail.filoghost.chestcommands.util.itemreader.ItemStackReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RequiredItem {

  private ItemStackReader itemReader;
  private boolean isDurabilityRestrictive = false;

  public RequiredItem(ItemStackReader itemReader) {
    Validate.notNull(itemReader.getMaterial(), "Material cannot be null");
    Validate.isTrue(itemReader.getMaterial() != Material.AIR, "Material cannot be air");

    this.itemReader = itemReader;

    if (itemReader.hasExplicitDataValue()) {
      Validate.isTrue(itemReader.getDataValue() >= 0, "Data value cannot be negative");

      isDurabilityRestrictive = true;
    }
  }

  public ItemStack createItemStack() {
    return itemReader.createStack();
  }

  public Material getMaterial() {
    return itemReader.getMaterial();
  }

  public int getAmount() {
    return itemReader.getAmount();
  }

  public short getDataValue() {
    return itemReader.getDataValue();
  }

  public boolean hasItemMeta() {
    return createItemStack().hasItemMeta();
  }

  public ItemMeta getItemMeta() {
    return createItemStack().getItemMeta();
  }

  public boolean hasRestrictiveDataValue() {
    return isDurabilityRestrictive;
  }

  public boolean isValidDataValue(short data) {
    if (!isDurabilityRestrictive) {
      return true;
    }
    return data == this.itemReader.getDataValue();
  }

  public boolean isValidItemMeta(ItemStack item) {
    return ChestCommands.getInstance().getServer().getItemFactory()
        .equals(item.getItemMeta(), createItemStack().getItemMeta());
  }

  public boolean hasItem(Player player) {
    int amountFound = 0;

    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && item.getType() == getMaterial() && isValidDataValue(item.getDurability())
          && isValidItemMeta(item)) {
        amountFound += item.getAmount();
      }
    }

    return amountFound >= getAmount();
  }

  public void takeItem(Player player) {
    if (getAmount() <= 0) {
      return;
    }

    int itemsToTake = getAmount(); //start from amount and decrease

    ItemStack[] contents = player.getInventory().getContents();
    ItemStack current;

    for (int i = 0; i < contents.length; i++) {

      current = contents[i];

      if (current != null && current.getType() == getMaterial() && isValidDataValue(
          current.getDurability()) && isValidItemMeta(current)) {
        if (current.getAmount() > itemsToTake) {
          current.setAmount(current.getAmount() - itemsToTake);
          return;
        } else {
          itemsToTake -= current.getAmount();
          player.getInventory().setItem(i, new ItemStack(Material.AIR));
        }
      }

      // The end
      if (itemsToTake <= 0) {
        return;
      }
    }
  }
}
