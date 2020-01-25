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
package com.gmail.filoghost.chestcommands.api;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.internal.VariableManager;
import com.gmail.filoghost.chestcommands.util.SkullUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Icon {

  protected boolean closeOnClick;
  private Material material;
  private int amount;
  private short dataValue;
  private String nbtData;
  private String name;
  private List<String> lore = Utils.newArrayList();
  private Map<Enchantment, Integer> enchantments;
  private Color color;
  private String skullOwner;
  private DyeColor bannerColor;
  private List<Pattern> bannerPatterns;
  private List<FireworkEffect> fireworkEffects;
  private ClickHandler clickHandler;

  private boolean nameHasVariables;
  private boolean[] loreLinesWithVariables = null;
  private boolean skullOwnerHasVariables;
  private ItemStack cachedItem; // When there are no variables, we don't recreate the item

  public Icon() {
    enchantments = new HashMap<>();
    closeOnClick = true;
    amount = 1;
  }

  public boolean hasVariables() {
    return nameHasVariables || loreLinesWithVariables != null || skullOwnerHasVariables;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    if (material == Material.AIR) {
      material = null;
    }
    this.material = material;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    if (amount < 1) {
      amount = 1;
    } else if (amount > 127) {
      amount = 127;
    }

    this.amount = amount;
  }

  public short getDataValue() {
    return dataValue;
  }

  public void setDataValue(short dataValue) {
    if (dataValue < 0) {
      dataValue = 0;
    }

    this.dataValue = dataValue;
  }

  public String getNBTData() {
    return nbtData;
  }

  public void setNBTData(String nbtData) {
    this.nbtData = nbtData;
  }

  public void setName(String name) {
    this.name = name;
    this.nameHasVariables = VariableManager.hasVariables(name);
  }

  public boolean hasName() {
    return name != null;
  }

  public boolean hasLore() {
    return lore != null && !lore.isEmpty();
  }

  public List<String> getLore() {
    return lore;
  }

  public void setLore(String... lore) {
    if (lore != null) {
      setLore(Arrays.asList(lore));
    }
  }

  public void setLore(List<String> lore) {
    this.lore.addAll(lore);

    for (int i = 0; i < lore.size(); i++) {
      if (VariableManager.hasVariables(lore.get(i))) {
        if (this.loreLinesWithVariables == null) {
          this.loreLinesWithVariables = new boolean[lore.size()];
        }
        loreLinesWithVariables[i] = true;
      }
    }
  }

  public Map<Enchantment, Integer> getEnchantments() {
    return new HashMap<>(enchantments);
  }

  public void setEnchantments(Map<Enchantment, Integer> enchantments) {
    if (enchantments == null) {
      this.enchantments.clear();
      return;
    }
    this.enchantments = enchantments;
  }

  public void addEnchantment(Enchantment ench) {
    addEnchantment(ench, 1);
  }

  public void addEnchantment(Enchantment ench, Integer level) {
    enchantments.put(ench, level);
  }

  public void removeEnchantment(Enchantment ench) {
    enchantments.remove(ench);
  }

  public void clearEnchantments() {
    enchantments.clear();
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getSkullOwner() {
    return skullOwner;
  }

  public void setSkullOwner(String skullOwner) {
    this.skullOwner = skullOwner;
    this.skullOwnerHasVariables = VariableManager.hasVariables(skullOwner);
  }

  public DyeColor getBannerColor() {
    return bannerColor;
  }

  public void setBannerColor(DyeColor bannerColor) {
    this.bannerColor = bannerColor;
  }

  public List<Pattern> getBannerPatterns() {
    return bannerPatterns;
  }

  public void setBannerPatterns(List<Pattern> bannerPatterns) {
    this.bannerPatterns = bannerPatterns;
  }

  public void setCloseOnClick(boolean closeOnClick) {
    this.closeOnClick = closeOnClick;
  }

  public ClickHandler getClickHandler() {
    return clickHandler;
  }

  public void setClickHandler(ClickHandler clickHandler) {
    this.clickHandler = clickHandler;
  }

  protected String calculateName(Player pov) {
    if (hasName()) {

      String displayname = this.name;

      if (pov != null && nameHasVariables) {
        displayname = VariableManager.setVariables(displayname, pov);
      }

      if (displayname.isEmpty()) {
        // Add a color to display the name empty
        return ChatColor.WHITE.toString();
      } else {
        return displayname;
      }
    }

    return null;
  }

  protected List<String> calculateLore(Player pov) {

    List<String> output = null;

    if (hasLore()) {

      output = Utils.newArrayList();

      if (pov != null && loreLinesWithVariables != null) {
        for (int i = 0; i < lore.size(); i++) {
          String line = lore.get(i);
          if (loreLinesWithVariables[i]) {
            line = VariableManager.setVariables(line, pov);
          }
          output.add(line);
        }
      } else {
        // Otherwise just copy the lines
        output.addAll(lore);
      }
    }

    if (material == null) {

      if (output == null) {
        output = Utils.newArrayList();
      }

      // Add an error message
      output.add(ChatColor.RED + "(Invalid material)");
    }

    return output;
  }

  @SuppressWarnings("deprecation")
  public ItemStack createItemstack(Player pov) {

    if (!this.hasVariables() && cachedItem != null) {
      // Performance
      return cachedItem;
    }

    // If the material is not set, display BEDROCK
    ItemStack itemStack = (material != null) ? new ItemStack(material, amount, dataValue)
        : new ItemStack(Material.BEDROCK, amount);

    // First try to apply NBT data
    if (nbtData != null) {
      try {
        // Note: this method should not throw any exception. It should log directly to the console
        Bukkit.getUnsafe().modifyItemStack(itemStack, nbtData);
      } catch (Exception t) {
        this.nbtData = null;
        ChestCommands.getInstance().getLogger()
            .log(Level.WARNING, "Could not apply NBT-DATA to an item.", t);
      }
    }

    // Then apply data from config nodes, overwriting NBT data if there are confliting values
    ItemMeta itemMeta = itemStack.getItemMeta();

    if (skullOwner != null && itemMeta instanceof SkullMeta) {
      String skull = this.skullOwner;
      if (skullOwnerHasVariables) {
        skull = VariableManager.setVariables(skull, pov);
      }
      itemMeta = SkullUtils.parseSkull(itemMeta, skull);
    }
    // In case the meta has lore, remove it
    itemMeta.setLore(Utils.newArrayList());

    if (hasName()) {
      itemMeta.setDisplayName(calculateName(pov));
    }
    if (hasLore()) {
      itemMeta.setLore(calculateLore(pov));
    }

    if (color != null && itemMeta instanceof LeatherArmorMeta) {
      ((LeatherArmorMeta) itemMeta).setColor(color);
    }

    if (itemMeta instanceof BlockStateMeta) {
      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      if (blockStateMeta.hasBlockState()) {
        BlockState blockState = blockStateMeta.getBlockState();
        if (blockState instanceof Banner) {
          Banner banner = (Banner) blockState;
          if (bannerColor != null) {
            banner.setBaseColor(bannerColor);
          }
          if (bannerPatterns != null) {
            banner.setPatterns(bannerPatterns);
          }
          banner.update();
          blockStateMeta.setBlockState(banner);
        }
      }
    } else if (itemMeta instanceof BannerMeta) {
      BannerMeta bannerMeta = (BannerMeta) itemMeta;
      if (bannerColor != null) {
        bannerMeta.setBaseColor(bannerColor);
      }
      if (bannerPatterns != null) {
        bannerMeta.setPatterns(bannerPatterns);
      }
    }

    if (fireworkEffects != null && !fireworkEffects.isEmpty()) {
      if (itemMeta instanceof FireworkMeta) {
        ((FireworkMeta) itemMeta).addEffects(fireworkEffects);
      } else if (itemMeta instanceof FireworkEffectMeta) {
        ((FireworkEffectMeta) itemMeta).setEffect(fireworkEffects.get(0));
      }
    }

    itemStack.setItemMeta(itemMeta);

    if (enchantments.size() > 0) {
      for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
        itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
      }
    }

    if (!this.hasVariables()) {
      // If there are no variables, cache the item
      cachedItem = itemStack;
    }

    return itemStack;
  }

  public boolean onClick(Player whoClicked, ClickType clickType) {
    if (clickHandler != null) {
      return clickHandler.onClick(whoClicked, clickType);
    }

    return closeOnClick;
  }

  public List<FireworkEffect> getFireworkEffects() {
    return fireworkEffects;
  }

  public void setFireworkEffects(List<FireworkEffect> fireworkEffects) {
    this.fireworkEffects = fireworkEffects;
  }
}
