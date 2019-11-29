package me.hsgamer.chestcommandslegacysupport;

import com.gmail.filoghost.chestcommands.api.event.IconCreateEvent;
import com.gmail.filoghost.chestcommands.internal.icon.ExtendedIcon;
import com.gmail.filoghost.chestcommands.internal.requirement.Requirements;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ConditionIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ExpLevelIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ItemIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.MoneyIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.PermissionIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.PointIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.TokenIconRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestCommandsLegacySupport extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll((Listener) this);
  }

  @EventHandler
  public void onIconCreate(IconCreateEvent event) {
    ExtendedIcon icon = event.getExtendedIcon();
    ConfigurationSection section = event.getConfigurationSection();

    Requirements requirements = icon.getRequirements();

    if (section.isSet(Nodes.PRICE)) {
      String value = section.getString(Nodes.PRICE, "0");
      MoneyIconRequirement requirement = new MoneyIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.POINTS)) {
      String value = section.getString(Nodes.POINTS, "0");
      PointIconRequirement requirement = new PointIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.TOKENS)) {
      String value = section.getString(Nodes.TOKENS, "0");
      TokenIconRequirement requirement = new TokenIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.EXP_LEVELS)) {
      String value = section.getString(Nodes.EXP_LEVELS, "0");
      ExpLevelIconRequirement requirement = new ExpLevelIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isConfigurationSection(Nodes.REQUIRED_ITEM)) {
      // LEFT REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_LEFT)) {
        String value = section.getString(Nodes.REQUIRED_ITEM_LEFT);
        ItemIconRequirement requirement = new ItemIconRequirement();
        requirement.setValue(value);
        requirements.addClickRequirement(requirement, ClickType.LEFT);
      }
      // RIGHT REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_RIGHT)) {
        String value = section.getString(Nodes.REQUIRED_ITEM_RIGHT);
        ItemIconRequirement requirement = new ItemIconRequirement();
        requirement.setValue(value);
        requirements.addClickRequirement(requirement, ClickType.RIGHT);
      }
      // MIDDLE REQUIRED ITEMS
      if (section.isSet(Nodes.REQUIRED_ITEM_MIDDLE)) {
        String value = section.getString(Nodes.REQUIRED_ITEM_MIDDLE);
        ItemIconRequirement requirement = new ItemIconRequirement();
        requirement.setValue(value);
        requirements.addClickRequirement(requirement, ClickType.MIDDLE);
      }
    } else if (section.isSet(Nodes.REQUIRED_ITEM)) {
      String value = section.getString(Nodes.REQUIRED_ITEM);
      ItemIconRequirement requirement = new ItemIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.PERMISSION)) {
      String value = section.getString(Nodes.PERMISSION);
      PermissionIconRequirement requirement = new PermissionIconRequirement();
      requirement.setValue(value);
      if (section.isSet(Nodes.PERMISSION_MESSAGE)) {
        requirement.setFailMessage(section.getString(Nodes.PERMISSION_MESSAGE));
      }
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.VIEW_PERMISSION)) {
      String value = section.getString(Nodes.VIEW_PERMISSION);
      PermissionIconRequirement requirement = new PermissionIconRequirement();
      requirement.setValue(value);
      requirements.addViewRequirement(requirement);
    }

    if (section.isSet(Nodes.VIEW_REQUIREMENT)) {
      String value = section.getString(Nodes.VIEW_REQUIREMENT);
      ConditionIconRequirement requirement = new ConditionIconRequirement();
      requirement.setValue(value);
      requirements.addViewRequirement(requirement);
    }

    if (section.isSet(Nodes.CLICK_REQUIREMENT)) {
      String value = section.getString(Nodes.CLICK_REQUIREMENT);
      ConditionIconRequirement requirement = new ConditionIconRequirement();
      requirement.setValue(value);
      if (section.isSet(Nodes.CLICK_REQUIREMENT_MESSAGE)) {
        requirement.setFailMessage(section.getString(Nodes.CLICK_REQUIREMENT_MESSAGE));
      }
      requirements.addDefaultClickRequirement(requirement);
    }
  }

  private static class Nodes {
    static final String PRICE = "PRICE";
    static final String POINTS = "POINTS";
    static final String TOKENS = "TOKENS";
    static final String EXP_LEVELS = "LEVELS";
    static final String REQUIRED_ITEM = "REQUIRED-ITEM";
    static final String REQUIRED_ITEM_LEFT = "REQUIRED-ITEM.LEFT";
    static final String REQUIRED_ITEM_RIGHT = "REQUIRED-ITEM.RIGHT";
    static final String REQUIRED_ITEM_MIDDLE = "REQUIRED-ITEM.MIDDLE";
    static final String PERMISSION = "PERMISSION";
    static final String PERMISSION_MESSAGE = "PERMISSION-MESSAGE";
    static final String VIEW_PERMISSION = "VIEW-PERMISSION";
    static final String VIEW_REQUIREMENT = "VIEW-REQUIREMENT";
    static final String CLICK_REQUIREMENT = "CLICK-REQUIREMENT";
    static final String CLICK_REQUIREMENT_MESSAGE = "CLICK-REQUIREMENT-MESSAGE";
  }
}
