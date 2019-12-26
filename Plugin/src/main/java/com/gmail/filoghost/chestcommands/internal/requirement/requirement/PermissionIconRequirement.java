package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import org.bukkit.entity.Player;

public class PermissionIconRequirement extends IconRequirement {

  public PermissionIconRequirement() {
    super(false, ValueType.STRING);
  }

  @Override
  public boolean check(Player player) {
    for (Object value : getParsedValue(player)) {
      if (!hasPermission(player, (String) value)) {
        if (failMessage != null) {
          player.sendMessage(failMessage.replace("{permission}", (String) value));
        } else {
          player.sendMessage(ChestCommands.getLang().default_no_icon_permission
              .replace("{permission}", (String) value));
        }
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission(Player player, String permission) {
    if (permission.startsWith("-")) {
      return !player.hasPermission(permission.substring(1).trim());
    } else {
      return player.hasPermission(permission);
    }
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
