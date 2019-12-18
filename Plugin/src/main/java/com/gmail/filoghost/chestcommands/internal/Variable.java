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
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.TokenManagerBridge;
import com.gmail.filoghost.chestcommands.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Variable {

  PLAYER("{player}") {
    public String getReplacement(Player executor) {
      return executor.getName();
    }
  },

  ONLINE("{online}") {
    public String getReplacement(Player executor) {
      return String.valueOf(CachedGetters.getOnlinePlayers());
    }
  },

  POINTS("{points}") {
    public String getReplacement(Player executor) {
      if (PlayerPointsBridge.hasValidPlugin()) {
        return String.valueOf(PlayerPointsBridge.getPoints(executor));
      } else {
        return "[PLAYER POINTS PLUGIN NOT FOUND]";
      }
    }
  },

  TOKENS("{tokens}") {
    public String getReplacement(Player executor) {
      if (TokenManagerBridge.hasValidPlugin()) {
        return String.valueOf(TokenManagerBridge.getTokens(executor));
      } else {
        return "[TOKEN MANAGER PLUGIN NOT FOUND]";
      }
    }
  },

  MAX_PLAYERS("{max_players}") {
    public String getReplacement(Player executor) {
      return String.valueOf(Bukkit.getMaxPlayers());
    }
  },

  MONEY("{money}") {
    public String getReplacement(Player executor) {
      if (VaultBridge.hasValidEconomy()) {
        return VaultBridge.formatMoney(VaultBridge.getMoney(executor));
      } else {
        return "[ECONOMY PLUGIN NOT FOUND]";
      }
    }
  },

  WORLD("{world}") {
    public String getReplacement(Player executor) {
      return executor.getWorld().getName();
    }
  },

  X("{x}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getLocation().getX());
    }
  },

  Y("{y}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getLocation().getY());
    }
  },

  Z("{z}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getLocation().getZ());
    }
  },

  BED_WORLD("{bed_world}") {
    public String getReplacement(Player executor) {
      return executor.getBedSpawnLocation().getWorld().getName();
    }
  },

  BED_X("{bed_x}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getBedSpawnLocation().getX());
    }
  },

  BED_Y("{bed_y}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getBedSpawnLocation().getY());
    }
  },

  BED_Z("{bed_z}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getBedSpawnLocation().getZ());
    }
  },

  EXP("{exp}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getTotalExperience());
    }
  },

  LEVEL("{level}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getLevel());
    }
  },

  EXP_TO_LEVEL("{exp_to_level}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getExpToLevel());
    }
  },

  FOOD_LEVEL("{food_level}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getFoodLevel());
    }
  },

  IP("{ip}") {
    public String getReplacement(Player executor) {
      return executor.getAddress().getAddress().getHostAddress();
    }
  },

  BIOME("{biome}") {
    public String getReplacement(Player executor) {
      return String.valueOf(executor.getLocation().getBlock().getBiome());
    }
  },

  PING("{ping}") {
    public String getReplacement(Player executor) {
      return BukkitUtils.getPing(executor);
    }
  },

  GROUP("{group}") {
    public String getReplacement(Player executor) {
      if (VaultBridge.hasValidPermission()) {
        return VaultBridge.getPrimaryGroup(executor);
      } else {
        return "[PERMISSION PLUGIN NOT FOUND]";
      }
    }
  },

  RAINBOW("{rainbow}") {
    public String getReplacement(Player executor) {
      ChatColor[] values = ChatColor.values();
      ChatColor color = null;
      while (color == null
          || color.equals(ChatColor.BOLD)
          || color.equals(ChatColor.ITALIC)
          || color.equals(ChatColor.STRIKETHROUGH)
          || color.equals(ChatColor.RESET)
          || color.equals(ChatColor.MAGIC)
          || color.equals(ChatColor.UNDERLINE)) {
        color = values[ChestCommands.getRandom().nextInt(values().length - 1)];
      }
      return "&" + color.getChar();
    }
  };

  private final String text;

  Variable(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public abstract String getReplacement(Player executor);
}
