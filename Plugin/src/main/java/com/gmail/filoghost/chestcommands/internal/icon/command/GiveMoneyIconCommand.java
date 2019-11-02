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
package com.gmail.filoghost.chestcommands.internal.icon.command;

import co.aikar.taskchain.TaskChain;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.internal.icon.IconCommand;
import com.gmail.filoghost.chestcommands.util.ExpressionUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GiveMoneyIconCommand extends IconCommand {

  private String errorMessage;

  public GiveMoneyIconCommand(String command) {
    super(command);
  }

  @Override
  public void execute(Player player, TaskChain taskChain) {
    double moneyToGive = 0;
    String parsed = getParsedCommand(player);
    if (Utils.isValidPositiveInteger(parsed)) {
      moneyToGive = Double.parseDouble(parsed);
    } else if (ExpressionUtils.isValidExpression(parsed)) {
      moneyToGive = Objects.requireNonNull(ExpressionUtils.getResult(parsed)).doubleValue();
    } else {
      errorMessage = ChatColor.RED + "Invalid money amount: " + command;
    }

    if (errorMessage != null) {
      player.sendMessage(errorMessage);
      return;
    }
    if (!VaultBridge.hasValidEconomy()) {
      player.sendMessage(ChatColor.RED
          + "Vault with a compatible economy plugin not found. Please inform the staff.");
      return;
    }

    if (moneyToGive > 0) {
      double finalMoneyToGive = moneyToGive;
      taskChain.sync(() -> VaultBridge.giveMoney(player, finalMoneyToGive));
    }
  }

}
