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
package com.gmail.filoghost.chestcommands;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import com.gmail.filoghost.chestcommands.bridge.BarAPIBridge;
import com.gmail.filoghost.chestcommands.bridge.PlaceholderAPIBridge;
import com.gmail.filoghost.chestcommands.bridge.TitleBridge;
import com.gmail.filoghost.chestcommands.bridge.VaultBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.PlayerPointsBridge;
import com.gmail.filoghost.chestcommands.bridge.currency.TokenManagerBridge;
import com.gmail.filoghost.chestcommands.bridge.heads.EpicHeadsBridge;
import com.gmail.filoghost.chestcommands.bridge.heads.HeadDatabaseBridge;
import com.gmail.filoghost.chestcommands.bridge.heads.HeadsPlusBridge;
import com.gmail.filoghost.chestcommands.command.CommandHandler;
import com.gmail.filoghost.chestcommands.command.framework.CommandFramework;
import com.gmail.filoghost.chestcommands.config.AsciiPlaceholders;
import com.gmail.filoghost.chestcommands.config.Lang;
import com.gmail.filoghost.chestcommands.config.Settings;
import com.gmail.filoghost.chestcommands.config.yaml.PluginConfig;
import com.gmail.filoghost.chestcommands.internal.BoundItem;
import com.gmail.filoghost.chestcommands.internal.ExtendedIconMenu;
import com.gmail.filoghost.chestcommands.internal.MenuData;
import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;
import com.gmail.filoghost.chestcommands.listener.CommandListener;
import com.gmail.filoghost.chestcommands.listener.InventoryListener;
import com.gmail.filoghost.chestcommands.listener.JoinListener;
import com.gmail.filoghost.chestcommands.listener.SignListener;
import com.gmail.filoghost.chestcommands.serializer.CommandSerializer;
import com.gmail.filoghost.chestcommands.serializer.MenuSerializer;
import com.gmail.filoghost.chestcommands.task.ErrorLoggerTask;
import com.gmail.filoghost.chestcommands.util.BukkitUtils;
import com.gmail.filoghost.chestcommands.util.CaseInsensitiveMap;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestCommands extends JavaPlugin {

  public static final String CHAT_PREFIX =
      ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "ChestCommands" + ChatColor.DARK_GREEN + "] "
          + ChatColor.GREEN;

  private static ChestCommands instance;
  private static Settings settings;
  private static Lang lang;

  private static Map<String, ExtendedIconMenu> fileNameToMenuMap;
  private static Map<String, ExtendedIconMenu> commandsToMenuMap;

  private static Set<BoundItem> boundItems;

  private static int lastReloadErrors;
  private static String newVersion;

  private static TaskChainFactory taskChainFactory;

  private static Random random = new Random();

  public static void closeAllMenus() {
    for (Player player : BukkitUtils.getOnlinePlayers()) {
      if (player.getOpenInventory() != null) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuInventoryHolder
            || player.getOpenInventory().getBottomInventory()
            .getHolder() instanceof MenuInventoryHolder) {
          player.closeInventory();
        }
      }
    }
  }

  public static boolean isSpigot() {
    return Utils.isClassLoaded("org.bukkit.entity.Player$Spigot");
  }

  public static ChestCommands getInstance() {
    return instance;
  }

  public static Settings getSettings() {
    return settings;
  }

  public static Lang getLang() {
    return lang;
  }

  public static boolean hasNewVersion() {
    return newVersion != null;
  }

  public static String getNewVersion() {
    return newVersion;
  }

  public static Map<String, ExtendedIconMenu> getFileNameToMenuMap() {
    return fileNameToMenuMap;
  }

  public static Map<String, ExtendedIconMenu> getCommandToMenuMap() {
    return commandsToMenuMap;
  }

  public static Set<BoundItem> getBoundItems() {
    return boundItems;
  }

  public static int getLastReloadErrors() {
    return lastReloadErrors;
  }

  public static void setLastReloadErrors(int lastReloadErrors) {
    ChestCommands.lastReloadErrors = lastReloadErrors;
  }

  public static TaskChainFactory getTaskChainFactory() {
    return taskChainFactory;
  }

  public static Random getRandom() {
    return random;
  }

  @Override
  public void onEnable() {
    if (instance != null) {
      getLogger()
          .warning("Please do not use /reload or plugin reloaders. Do \"/cc reload\" instead.");
      return;
    }

    instance = this;
    fileNameToMenuMap = CaseInsensitiveMap.create();
    commandsToMenuMap = CaseInsensitiveMap.create();
    boundItems = Utils.newHashSet();

    settings = new Settings(new PluginConfig(this, "config.yml"));
    lang = new Lang(new PluginConfig(this, "lang.yml"));

    taskChainFactory = BukkitTaskChainFactory.create(this);

    if (!VaultBridge.setupEconomy()) {
      getLogger().warning(
          "Vault with a compatible economy plugin was not found! Icons with a PRICE or commands that give money will not work.");
    }

    if (!VaultBridge.setupPermission()) {
      getLogger().warning(
          "Vault with a compatible permission plugin was not found! Variable {group} will not work.");
    }

    if (BarAPIBridge.setupPlugin()) {
      getLogger().info("Hooked BarAPI");
    }

    if (PlaceholderAPIBridge.setupPlugin()) {
      getLogger().info("Hooked PlaceholderAPI");
    }

    if (PlayerPointsBridge.setupPlugin()) {
      getLogger().info("Hooked PlayerPoints");
    }

    if (TokenManagerBridge.setupPlugin()) {
      getLogger().info("Hooked TokenManager");
    }

    if (HeadDatabaseBridge.setupPlugin()) {
      getLogger().info("Hooked HeadDatabase");
    }

    if (HeadsPlusBridge.setupPlugin()) {
      getLogger().info("Hooked HeadsPlus");
    }

    if (EpicHeadsBridge.setupPlugin()) {
      getLogger().info("Hooked EpicHeads");
    }

    if (TitleBridge.setupPlugin()) {
      getLogger().info("Enabled Title features");
    }

    if (settings.update_notifications) {
      new SimpleUpdater(this, 56919).checkForUpdates(newVersion -> {
        ChestCommands.newVersion = newVersion;

        if (settings.use_console_colors) {
          Bukkit.getConsoleSender().sendMessage(
              CHAT_PREFIX + "Found a new version: " + newVersion + ChatColor.WHITE + " (yours: v"
                  + getDescription().getVersion() + ")");
          Bukkit.getConsoleSender()
              .sendMessage(CHAT_PREFIX + ChatColor.WHITE + "Download it on Bukkit Dev:");
          Bukkit.getConsoleSender().sendMessage(
              CHAT_PREFIX + ChatColor.WHITE + "dev.bukkit.org/bukkit-plugins/chest-commands");
        } else {
          getLogger().info("Found a new version available: " + newVersion);
          getLogger().info("Download it on Bukkit Dev:");
          getLogger().info("dev.bukkit.org/bukkit-plugins/chest-commands");
        }
      });
    }

    // Start bStats metrics
    new MetricsLite(this);

    Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
    Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    Bukkit.getPluginManager().registerEvents(new SignListener(), this);

    CommandFramework.register(this, new CommandHandler("chestcommands"));

    ErrorLogger errorLogger = new ErrorLogger();
    load(errorLogger);

    lastReloadErrors = errorLogger.getSize();
    if (errorLogger.hasErrors()) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ErrorLoggerTask(errorLogger), 10L);
    }
  }

  @Override
  public void onDisable() {
    closeAllMenus();
  }

  public void load(ErrorLogger errorLogger) {
    fileNameToMenuMap.clear();
    commandsToMenuMap.clear();
    boundItems.clear();

    CommandSerializer.checkClassConstructors(errorLogger);

    try {
      settings.load();
    } catch (IOException e) {
      getLogger().log(Level.WARNING,
          "I/O error while using the configuration. Default values will be used.", e);
    } catch (InvalidConfigurationException e) {
      getLogger().log(Level.WARNING,
          "The config.yml was not a valid YAML, please look at the error above. Default values will be used.",
          e);
    } catch (Exception e) {
      getLogger().log(Level.WARNING,
          "Unhandled error while reading the values for the configuration! Please inform the developer.",
          e);
    }

    try {
      lang.load();
    } catch (IOException e) {
      getLogger().log(Level.WARNING,
          "I/O error while using the language file. Default values will be used.", e);
    } catch (InvalidConfigurationException e) {
      getLogger().log(Level.WARNING,
          "The lang.yml was not a valid YAML, please look at the error above. Default values will be used.",
          e);
    } catch (Exception e) {
      getLogger().log(Level.WARNING,
          "Unhandled error while reading the values for the language file! Please inform the developer.",
          e);
    }

    try {
      AsciiPlaceholders.load(errorLogger);
    } catch (IOException e) {
      getLogger()
          .log(Level.WARNING, "I/O error while reading the placeholders. They will not work.", e);
    } catch (Exception e) {
      getLogger().log(Level.WARNING,
          "Unhandled error while reading the placeholders! Please inform the developer.", e);
    }

    // Load the menus
    File menusFolder = new File(getDataFolder(), "menu");

    if (!menusFolder.isDirectory()) {
      // Create the directory with the default menu
      menusFolder.mkdirs();
      BukkitUtils.saveResourceSafe(this, "menu" + File.separator + "example.yml");
      BukkitUtils.saveResourceSafe(this, "menu" + File.separator + "custom_gui.yml");
    }

    List<PluginConfig> menusList = loadMenus(menusFolder);
    for (PluginConfig menuConfig : menusList) {
      try {
        menuConfig.load();
      } catch (IOException e) {
        getLogger().log(Level.WARNING,
            "I/O error while loading the menu \"" + menuConfig.getFileName() + "\"", e);
        errorLogger.addError("I/O error while loading the menu \"" + menuConfig.getFileName()
            + "\". Is the file in use?");
        continue;
      } catch (InvalidConfigurationException e) {
        getLogger().log(Level.WARNING,
            "Invalid YAML configuration for the menu \"" + menuConfig.getFileName() + "\"", e);
        errorLogger.addError("Invalid YAML configuration for the menu \"" + menuConfig.getFileName()
            + "\". Please look at the error above, or use an online YAML parser (google is your friend).");
        continue;
      }

      MenuData data = MenuSerializer.loadMenuData(menuConfig, errorLogger);
      ExtendedIconMenu iconMenu = MenuSerializer
          .loadMenu(menuConfig, data.getTitle(), data.getSlots(), data.getInventoryType(),
              errorLogger);

      if (fileNameToMenuMap.containsKey(menuConfig.getFileName())) {
        errorLogger.addError("Two menus have the same file name \"" + menuConfig.getFileName()
            + "\" with different cases. There will be problems opening one of these two menus.");
      }
      fileNameToMenuMap.put(menuConfig.getFileName(), iconMenu);

      if (data.hasCommands()) {
        for (String command : data.getCommands()) {
          if (!command.isEmpty()) {
            if (commandsToMenuMap.containsKey(command)) {
              errorLogger.addError(
                  "The menus \"" + commandsToMenuMap.get(command).getFileName() + "\" and \""
                      + menuConfig.getFileName() + "\" have the same command \"" + command
                      + "\". Only one will be opened.");
            }
            commandsToMenuMap.put(command, iconMenu);
          }
        }
      }

      iconMenu.setRefreshTicks(data.getRefreshTenths());

      if (data.getOpenActions() != null) {
        iconMenu.setOpenActions(data.getOpenActions());
      }

      if (data.hasBoundMaterial() && data.getClickType() != null) {
        BoundItem boundItem = new BoundItem(iconMenu, data.getBoundMaterial(), data.getClickType());
        if (data.hasBoundDataValue()) {
          boundItem.setRestrictiveData(data.getBoundDataValue());
        }
        boundItems.add(boundItem);
      }
    }

    // Register the BungeeCord plugin channel
    if (!Bukkit.getMessenger().isOutgoingChannelRegistered(this, "BungeeCord")) {
      Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
  }

  /**
   * Loads all the configuration files recursively into a list.
   */
  private List<PluginConfig> loadMenus(File file) {
    List<PluginConfig> list = Utils.newArrayList();
    if (file.isDirectory()) {
      for (File subFile : file.listFiles()) {
        list.addAll(loadMenus(subFile));
      }
    } else if (file.isFile() && file.getName().endsWith(".yml")) {
      list.add(new PluginConfig(this, file));
    }
    return list;
  }

}
