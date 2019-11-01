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

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.Icon;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.TokenManagerBridge;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;
import com.gmail.filoghost.chestcommands.internal.RequiredItem;
import com.gmail.filoghost.chestcommands.internal.VariableManager;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import com.gmail.filoghost.chestcommands.util.ItemUtils;
import com.gmail.filoghost.chestcommands.util.MaterialsRegistry;
import com.gmail.filoghost.chestcommands.util.StringUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ExtendedIcon extends Icon {

  private String permission;
  private String permissionMessage;
  private String viewPermission;

  private String viewRequirement;
  private String clickRequirement;
  private String clickRequirementMessage;

  private boolean permissionNegated;
  private boolean viewPermissionNegated;

  private String money;
  private String expLevels;
  private List<RequiredItem> leftRequiredItems = new ArrayList<>();
  private List<RequiredItem> rightRequiredItems = new ArrayList<>();
  private List<RequiredItem> middleRequiredItems = new ArrayList<>();
  private String playerPoints;
  private String tokenManager;

  private long leftCooldown = 0;
  private long rightCooldown = 0;
  private long middleCooldown = 0;
  private boolean cooldownAll = false;
  private Map<Player, Long> leftCooldownList = Utils.newHashMap();
  private Map<Player, Long> rightCooldownList = Utils.newHashMap();
  private Map<Player, Long> middleCooldownList = Utils.newHashMap();
  private String cooldownMessage;

  public ExtendedIcon() {
    super();
  }

  public boolean hasClickPermission(Player player) {
    if (permission == null) {
      return true;
    }

    if (permissionNegated) {
      return !player.hasPermission(permission);
    } else {
      return player.hasPermission(permission);
    }
  }

  public void setPermission(String permission) {
    if (StringUtils.isNullOrEmpty(permission)) {
      permission = null;
    }

    if (permission != null) {
      if (permission.startsWith("-")) {
        permissionNegated = true;
        permission = permission.substring(1).trim();
      }
    }
    this.permission = permission;
  }

  public String getPermissionMessage() {
    return permissionMessage;
  }

  public void setPermissionMessage(String permissionMessage) {
    this.permissionMessage = permissionMessage;
  }

  public boolean hasViewPermission() {
    return viewPermission != null;
  }

  public boolean hasViewRequirement() {
    return viewRequirement != null;
  }

  public boolean hasViewPermission(Player player) {
    if (viewPermission == null) {
      return true;
    }

    if (viewPermissionNegated) {
      return !player.hasPermission(viewPermission);
    } else {
      return player.hasPermission(viewPermission);
    }
  }

  public void setViewPermission(String viewPermission) {
    if (StringUtils.isNullOrEmpty(viewPermission)) {
      viewPermission = null;
    }

    if (viewPermission != null) {
      if (viewPermission.startsWith("-")) {
        viewPermissionNegated = true;
        viewPermission = viewPermission.substring(1).trim();
      }
    }
    this.viewPermission = viewPermission;
  }

  public String getMoneyPrice() {
    return money;
  }

  public void setMoneyPrice(String moneyPrice) {
    this.money = moneyPrice;
  }

  public String getPlayerPointsPrice() {
    return playerPoints;
  }

  public void setPlayerPointsPrice(String playerPointsPrice) {
    this.playerPoints = playerPointsPrice;
  }

  public String getTokenManagerPrice() {
    return tokenManager;
  }

  public void setTokenManagerPrice(String tokenManagerPrice) {
    this.tokenManager = tokenManagerPrice;
  }

  public String getExpLevelsPrice() {
    return expLevels;
  }

  public void setExpLevelsPrice(String expLevelsPrice) {
    this.expLevels = expLevelsPrice;
  }

  public List<RequiredItem> getLeftRequiredItems() {
    return leftRequiredItems;
  }

  public void setLeftRequiredItems(List<RequiredItem> requiredItems) {
    this.leftRequiredItems = requiredItems;
  }

  public List<RequiredItem> getRightRequiredItems() {
    return rightRequiredItems;
  }

  public void setRightRequiredItems(List<RequiredItem> requiredItems) {
    this.rightRequiredItems = requiredItems;
  }

  public List<RequiredItem> getMiddleRequiredItems() {
    return middleRequiredItems;
  }

  public void setMiddleRequiredItems(List<RequiredItem> requiredItems) {
    this.middleRequiredItems = requiredItems;
  }

  public void setRequiredItems(List<RequiredItem> requiredItems) {
    this.leftRequiredItems = requiredItems;
    this.rightRequiredItems = requiredItems;
    this.middleRequiredItems = requiredItems;
  }

  public String calculateName(Player pov) {
    return super.calculateName(pov);
  }

  public List<String> calculateLore(Player pov) {
    return super.calculateLore(pov);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean onClick(Player player, ClickType clickType) {

    // Check all the requirements

    if (!hasClickPermission(player)) {
      if (permissionMessage != null) {
        player.sendMessage(permissionMessage);
      } else {
        player.sendMessage(ChestCommands.getLang().default_no_icon_permission);
      }
      return closeOnClick;
    }

    if (!hasClickRequirement(player)) {
      if (clickRequirementMessage != null) {
        player.sendMessage(clickRequirementMessage);
      } else {
        player.sendMessage(ChestCommands.getLang().default_no_requirement_message);
      }
      return closeOnClick;
    }

    double moneyPrice;
    String parsedMoney =
        VariableManager.hasVariables(money) ? VariableManager.setVariables(money, player) : money;
    if (ExpressionUtils.isValidExpression(parsedMoney)) {
      moneyPrice = ExpressionUtils.getResult(parsedMoney).doubleValue();
    } else {
      try {
        moneyPrice = Double.parseDouble(parsedMoney);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedMoney + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return closeOnClick;
      }
    }
    if (moneyPrice > 0) {
      if (!VaultBridge.hasValidEconomy()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
        return closeOnClick;
      }

      if (!VaultBridge.hasMoney(player, moneyPrice)) {
        player.sendMessage(ChestCommands.getLang().no_money
            .replace("{money}", VaultBridge.formatMoney(moneyPrice)));
        return closeOnClick;
      }
    }

    int playerPointsPrice;
    String parsedPoints = VariableManager.hasVariables(playerPoints) ? VariableManager
        .setVariables(playerPoints, player) : playerPoints;
    if (ExpressionUtils.isValidExpression(parsedPoints)) {
      playerPointsPrice = ExpressionUtils.getResult(parsedPoints).intValue();
    } else {
      try {
        playerPointsPrice = Integer.parseInt(parsedPoints);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedPoints + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return closeOnClick;
      }
    }
    if (playerPointsPrice > 0) {
      if (!PlayerPointsBridge.hasValidPlugin()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
        return closeOnClick;
      }

      if (!PlayerPointsBridge.hasPoints(player, playerPointsPrice)) {
        player.sendMessage(ChestCommands.getLang().no_points
            .replace("{points}", Integer.toString(playerPointsPrice)));
        return closeOnClick;
      }
    }

    long tokenManagerPrice;
    String parsedTokens = VariableManager.hasVariables(tokenManager) ? VariableManager
        .setVariables(tokenManager, player) : tokenManager;
    if (ExpressionUtils.isValidExpression(parsedTokens)) {
      tokenManagerPrice = ExpressionUtils.getResult(parsedTokens).longValue();
    } else {
      try {
        tokenManagerPrice = Long.parseLong(parsedTokens);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedTokens + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return closeOnClick;
      }
    }
    if (tokenManagerPrice > 0) {
      if (!TokenManagerBridge.hasValidPlugin()) {
        player.sendMessage(ChatColor.RED
            + "This command has a price in tokens, but the plugin TokenManager was not found. For security, the command has been blocked. Please inform the staff.");
        return closeOnClick;
      }

      if (!TokenManagerBridge.hasTokens(player, tokenManagerPrice)) {
        player.sendMessage(ChestCommands.getLang().no_tokens
            .replace("{tokens}", Long.toString(tokenManagerPrice)));
        return closeOnClick;
      }
    }

    int expLevelsPrice;
    String parsedExp =
        VariableManager.hasVariables(expLevels) ? VariableManager.setVariables(expLevels, player)
            : expLevels;
    if (ExpressionUtils.isValidExpression(parsedExp)) {
      expLevelsPrice = ExpressionUtils.getResult(parsedExp).intValue();
    } else {
      try {
        expLevelsPrice = Integer.parseInt(parsedExp);
      } catch (NumberFormatException e) {
        String error =
            ChatColor.RED + "Error parsing value!" + parsedExp + " is not a valid number";
        player.sendMessage(error);
        ChestCommands.getInstance().getLogger().warning(error);
        return closeOnClick;
      }
    }
    if (expLevelsPrice > 0) {
      if (player.getLevel() < expLevelsPrice) {
        player.sendMessage(
            ChestCommands.getLang().no_exp.replace("{levels}", Integer.toString(expLevelsPrice)));
        return closeOnClick;
      }
    }

    List<RequiredItem> requiredItems = new ArrayList<>();
    switch (clickType) {
      case LEFT:
        requiredItems = leftRequiredItems;
        break;
      case RIGHT:
        requiredItems = rightRequiredItems;
        break;
      case MIDDLE:
        requiredItems = middleRequiredItems;
        break;
    }
    if (!requiredItems.isEmpty()) {

      boolean notHasItem = false;

      for (RequiredItem requiredItem : requiredItems) {

        if (!requiredItem.hasItem(player)) {
          notHasItem = true;
          String message = ChestCommands.getLang().no_required_item
              .replace("{item}",
                  (requiredItem.hasItemMeta() && requiredItem.getItemMeta().hasDisplayName())
                      ? requiredItem.getItemMeta().getDisplayName()
                      : MaterialsRegistry.formatMaterial(requiredItem.getMaterial()))
              .replace("{amount}", Integer.toString(requiredItem.getAmount()))
              .replace("{datavalue}", requiredItem.hasRestrictiveDataValue() ? Short
                  .toString(requiredItem.getDataValue()) : ChestCommands.getLang().any);
          if (ChestCommands.isSpigot() && ChestCommands
              .getSettings().use_hover_event_on_required_item_message) {
            String itemJson = ItemUtils.convertItemStackToJson(requiredItem.createItemStack());

            BaseComponent[] hoverEventComponents = new BaseComponent[]{new TextComponent(itemJson)};

            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

            TextComponent component = new TextComponent(message);
            component.setHoverEvent(event);

            player.spigot().sendMessage(component);
          } else {
            player.sendMessage(message);
          }
        }

      }

      if (notHasItem) {
        return closeOnClick;
      }

    }

    long now = System.currentTimeMillis();
    Long cooldownUntil;
    if (cooldownAll) {
      cooldownUntil = leftCooldownList.get(player);
      if (leftCooldown > 0) {
        if (cooldownUntil != null && cooldownUntil > now) {
          if (cooldownMessage != null) {
            player.sendMessage(
                cooldownMessage.replace("{cooldown}", String.valueOf(cooldownUntil - now))
                    .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000)));
          } else {
            player.sendMessage(ChestCommands.getLang().default_cooldown_message
                .replace("{cooldown}", String.valueOf(cooldownUntil - now))
                .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000)));
          }
          return closeOnClick;
        } else {
          leftCooldownList.put(player, now + leftCooldown);
        }
      }
    } else {
      switch (clickType) {
        case LEFT:
          cooldownUntil = leftCooldownList.get(player);
          if (leftCooldown > 0) {
            if (cooldownUntil != null && cooldownUntil > now) {
              if (cooldownMessage != null) {
                player.sendMessage(
                    cooldownMessage.replace("{cooldown}", String.valueOf(cooldownUntil - now))
                        .replace("{cooldown_second}",
                            String.valueOf((cooldownUntil - now) / 1000)));
              } else {
                player.sendMessage(ChestCommands.getLang().default_cooldown_message
                    .replace("{cooldown}", String.valueOf(cooldownUntil - now))
                    .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000)));
              }
              return closeOnClick;
            } else {
              leftCooldownList.put(player, now + leftCooldown);
            }
          }
          break;
        case RIGHT:
          cooldownUntil = rightCooldownList.get(player);
          if (rightCooldown > 0) {
            if (cooldownUntil != null && cooldownUntil > now) {
              if (cooldownMessage != null) {
                player.sendMessage(
                    cooldownMessage.replace("{cooldown}", String.valueOf(cooldownUntil - now))
                        .replace("{cooldown_second}",
                            String.valueOf((cooldownUntil - now) / 1000)));
              } else {
                player.sendMessage(ChestCommands.getLang().default_cooldown_message
                    .replace("{cooldown}", String.valueOf(cooldownUntil - now))
                    .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000)));
              }
              return closeOnClick;
            } else {
              rightCooldownList.put(player, now + rightCooldown);
            }
          }
          break;
        case MIDDLE:
          cooldownUntil = middleCooldownList.get(player);
          if (middleCooldown > 0) {
            if (cooldownUntil != null && cooldownUntil > now) {
              if (cooldownMessage != null) {
                player.sendMessage(
                    cooldownMessage.replace("{cooldown}", String.valueOf(cooldownUntil - now))
                        .replace("{cooldown_second}",
                            String.valueOf((cooldownUntil - now) / 1000)));
              } else {
                player.sendMessage(ChestCommands.getLang().default_cooldown_message
                    .replace("{cooldown}", String.valueOf(cooldownUntil - now))
                    .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000)));
              }
              return closeOnClick;
            } else {
              middleCooldownList.put(player, now + middleCooldown);
            }
          }
          break;
      }
    }

    // Take the money, the points, the tokens and the required item

    boolean changedVariables = false; // To update the placeholders

    if (moneyPrice > 0) {
      if (!VaultBridge.takeMoney(player, moneyPrice)) {
        player.sendMessage(ChatColor.RED
            + "Error: the transaction couldn't be executed. Please inform the staff.");
        return closeOnClick;
      }
      changedVariables = true;
    }

    if (expLevelsPrice > 0) {
      player.setLevel(player.getLevel() - expLevelsPrice);
    }

    if (playerPointsPrice > 0) {
      if (!PlayerPointsBridge.takePoints(player, playerPointsPrice)) {
        player.sendMessage(ChatColor.RED
            + "Error: the transaction couldn't be executed. Please inform the staff.");
        return closeOnClick;
      }
      changedVariables = true;
    }

    if (tokenManagerPrice > 0) {
      if (!TokenManagerBridge.takeTokens(player, tokenManagerPrice)) {
        player.sendMessage(ChatColor.RED
            + "Error: the transaction couldn't be executed. Please inform the staff.");
        return closeOnClick;
      }
      changedVariables = true;
    }

    if (!requiredItems.isEmpty()) {
      requiredItems.forEach(requiredItem -> requiredItem.takeItem(player));
    }

    if (changedVariables) {
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
    }

    return super.onClick(player, clickType);
  }

  public String getCooldownMessage() {
    return cooldownMessage;
  }

  public void setCooldownMessage(String cooldownMessage) {
    this.cooldownMessage = cooldownMessage;
  }

  public void setLeftCooldown(long leftCooldown) {
    this.leftCooldown = leftCooldown;
  }

  public void setRightCooldown(long rightCooldown) {
    this.rightCooldown = rightCooldown;
  }

  public void setMiddleCooldown(long middleCooldown) {
    this.middleCooldown = middleCooldown;
  }

  public void setCooldownAll(boolean cooldownAll) {
    this.cooldownAll = cooldownAll;
  }

  public void setViewRequirement(String viewRequirement) {
    if (!StringUtils.isNullOrEmpty(viewRequirement)) {
      this.viewRequirement = viewRequirement;
    }
  }

  public void setClickRequirement(String clickRequirement) {
    if (!StringUtils.isNullOrEmpty(clickRequirement)) {
      this.clickRequirement = clickRequirement;
    }
  }

  public String getClickRequirementMessage() {
    return clickRequirementMessage;
  }

  public void setClickRequirementMessage(String clickRequirementMessage) {
    this.clickRequirementMessage = clickRequirementMessage;
  }

  public boolean hasClickRequirement(Player player) {
    if (StringUtils.isNullOrEmpty(clickRequirement)) {
      return true;
    }

    String parsed = VariableManager.hasVariables(clickRequirement) ? VariableManager
        .setVariables(clickRequirement, player) : clickRequirement;

    if (!ExpressionUtils.isBoolean(parsed)) {
      player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
      return false;
    }

    return ExpressionUtils.getResult(parsed).intValue() == 1;
  }

  public boolean hasViewRequirement(Player player) {
    if (StringUtils.isNullOrEmpty(viewRequirement)) {
      return true;
    }

    String parsed = VariableManager.hasVariables(viewRequirement) ? VariableManager
        .setVariables(viewRequirement, player) : viewRequirement;

    if (!ExpressionUtils.isBoolean(parsed)) {
      player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
      return false;
    }

    return ExpressionUtils.getResult(parsed).intValue() == 1;
  }
}
