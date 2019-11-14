package com.gmail.filoghost.chestcommands.internal;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Cooldown {

  private long leftTime = 0;
  private long rightTime = 0;
  private long middleTime = 0;
  private boolean all = false;
  private Map<Player, Long> leftCooldownList = Utils.newHashMap();
  private Map<Player, Long> rightCooldownList = Utils.newHashMap();
  private Map<Player, Long> middleCooldownList = Utils.newHashMap();
  private String cooldownMessage;

  public boolean isCooldown(Player player, ClickType clickType) {
    long now = System.currentTimeMillis();
    Long cooldownUntil = null;
    Map<Player, Long> cooldownList = null;
    long time = 0;
    if (all || clickType == ClickType.LEFT) {
      cooldownUntil = leftCooldownList.get(player);
      cooldownList = leftCooldownList;
      time = leftTime;
    } else if (clickType == ClickType.RIGHT) {
      cooldownUntil = rightCooldownList.get(player);
      cooldownList = rightCooldownList;
      time = rightTime;
    } else if (clickType == ClickType.MIDDLE) {
      cooldownUntil = middleCooldownList.get(player);
      cooldownList = middleCooldownList;
      time = middleTime;
    }
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
    switch (clickType) {
      case LEFT:
        this.leftTime = time;
        break;
      case RIGHT:
        this.rightTime = time;
        break;
      case MIDDLE:
        this.middleTime = time;
        break;
      default:
        break;
    }
  }

  public void setAll() {
    this.all = true;
  }

  public void setCooldownMessage(String cooldownMessage) {
    this.cooldownMessage = cooldownMessage;
  }
}
