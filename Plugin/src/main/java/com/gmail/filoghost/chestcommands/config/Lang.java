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
package com.gmail.filoghost.chestcommands.config;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.config.yaml.PluginConfig;
import com.gmail.filoghost.chestcommands.config.yaml.SpecialConfig;
import java.io.IOException;
import java.lang.reflect.Field;
import org.bukkit.configuration.InvalidConfigurationException;

public class Lang extends SpecialConfig {

  public String no_open_permission = "&cYou don't have permission &e{permission} &cto use this menu.";
  public String default_no_icon_permission = "&cYou don't have permission for this icon.";
  public String default_cooldown_message = "&cYou need to wait {cooldown} ({cooldown_second} secs) to do that";
  public String default_no_requirement_message = "&cYou don't meet the requirement for this icon";
  public String no_required_item = "&cYou must have &e{amount}x {item} &c(data value: {datavalue}) for this.";
  public String no_money = "&cYou need {money}$ for this.";
  public String no_exp = "&cYou need {levels} XP levels for this.";
  public String no_points = "&cYou need {points} player points for this.";
  public String no_tokens = "&cYou need {tokens} tokens for this.";
  public String menu_not_found = "&cMenu not found! Please inform the staff.";
  public String open_menu = "&aOpening the menu \"{menu}\".";
  public String open_menu_others = "&aOpening the menu \"{menu}\" to {player}.";
  public String any = "any"; // Used in no_required_item when data value is not restrictive

  public Lang(PluginConfig config) {
    super(config);
  }

  @Override
  public void load() throws IOException, InvalidConfigurationException, IllegalAccessException {
    super.load();

    for (Field field : getClass().getDeclaredFields()) {
      Class<?> type = field.getType();
      if (type == String.class) {
        try {
          field.set(this, AsciiPlaceholders.placeholdersToSymbols((String) field.get(this)));
        } catch (IllegalAccessException e) {
          ChestCommands.getInstance().getLogger()
              .warning("Error when getting the field " + field.getName());
        }
      }
    }
  }
}
