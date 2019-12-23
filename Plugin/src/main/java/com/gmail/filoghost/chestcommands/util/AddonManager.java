package com.gmail.filoghost.chestcommands.util;

import com.gmail.filoghost.chestcommands.api.Addon;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class AddonManager {

  private Map<String, Addon> addons = Utils.newHashMap();
  private File addonsDir;
  private Plugin plugin;

  public AddonManager(Plugin plugin) {
    this.plugin = plugin;
    addonsDir = new File(plugin.getDataFolder(), "addon");
    if (!addonsDir.isDirectory()) {
      addonsDir.mkdirs();
    }
  }

  public void loadAddons(ErrorLogger logger) {
    for (File file : addonsDir.listFiles()) {
      try (JarFile jar = new JarFile(file)) {
        Set<String> classes = new HashSet<>();
        ClassLoader loader = URLClassLoader.newInstance(
            new URL[]{file.toURI().toURL()},
            getClass().getClassLoader()
        );
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
            continue;
          }
          classes.add(entry.getName().substring(0, entry.getName().length() - 6).replace("/", "."));
        }
        for (String classpath : classes) {
          Class<?> clazz = Class.forName(classpath, true, loader);
          if (Addon.class.isAssignableFrom(clazz)) {
            Class<? extends Addon> newClass = clazz.asSubclass(Addon.class);
            Constructor<? extends Addon> constructor = newClass.getConstructor();
            Addon addon = constructor.newInstance();
            addons.put(addon.getName(), addon);
          }
        }
      } catch (ClassNotFoundException e) {
        // Ignored
      } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
        plugin.getLogger().log(Level.WARNING, "Error when loading jar", e);
        logger.addError("A problem occurred when loading " + file.getName());
      }
    }
  }

  public void enableAddon(String name) {
    addons.get(name).onEnable();
    plugin.getLogger().log(Level.INFO, "Enabled {0}", name);
  }

  public void disableAddon(String name) {
    addons.get(name).onDisable();
    plugin.getLogger().log(Level.INFO, "Disabled {0}", name);
  }

  public void enableAddons() {
    addons.keySet().forEach(this::enableAddon);
  }

  public void disableAddons() {
    addons.keySet().forEach(this::disableAddon);
  }

  public void reloadAddons(ErrorLogger errorLogger) {
    disableAddons();
    addons.clear();
    loadAddons(errorLogger);
    enableAddons();
  }
}
