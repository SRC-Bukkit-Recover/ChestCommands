package com.gmail.filoghost.chestcommands.internal.requirement.requirement;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.api.IconRequirement;
import org.bukkit.entity.Player;

public class PermissionIconRequirement extends IconRequirement {

  public PermissionIconRequirement() {
    super(false);
  }

  @Override
  public boolean check(Player player) {
    if (!hasPermission(player)) {
      if (failMessage != null) {
        player.sendMessage(failMessage.replace("{permission}", getParsedValue(player)));
      } else {
        player.sendMessage(ChestCommands.getLang().default_no_icon_permission
            .replace("{permission}", getParsedValue(player)));
      }
      return false;
    }
    return true;
  }

  private boolean hasPermission(Player player) {
    String permission = getParsedValue(player);
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
