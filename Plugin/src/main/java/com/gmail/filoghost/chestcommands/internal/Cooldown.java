package com.gmail.filoghost.chestcommands.internal;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Cooldown {

  private Map<ClickType, Long> cooldownTimePerType = Utils.newHashMap();
  private long defaultCooldownTime = 0;
  private Map<ClickType, Map<Player, Long>> cooldownListPerType = Utils.newHashMap();
  private Map<Player, Long> defaultCooldownList = Utils.newHashMap();
  private String cooldownMessage;

  public boolean isCooldown(Player player, ClickType clickType) {
    long now = System.currentTimeMillis();
    Map<Player, Long> cooldownList = cooldownListPerType
        .getOrDefault(clickType, defaultCooldownList);
    Long cooldownUntil = cooldownList.get(player);
    long time = cooldownTimePerType.getOrDefault(clickType, defaultCooldownTime);
    if (time > 0) {
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
        return true;
      } else {
        cooldownList.put(player, now + time);
      }
    }
    return false;
  }

  public void setTime(long time, ClickType clickType) {
    this.cooldownTimePerType.put(clickType, time);
    this.cooldownListPerType.put(clickType, Utils.newHashMap());
  }

  public void setDefaultTime(long time) {
    this.defaultCooldownTime = time;
  }

  public void setCooldownMessage(String cooldownMessage) {
    this.cooldownMessage = cooldownMessage;
  }
}
