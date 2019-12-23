package me.hsgamer.chestcommandslegacysupport;

import com.gmail.filoghost.chestcommands.api.Addon;
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

public final class ChestCommandsLegacySupport extends Addon implements Listener {

  public ChestCommandsLegacySupport() {
    super("ChestCommandsLegacySupport");
  }

  @Override
  public void onEnable() {
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onIconCreate(IconCreateEvent event) {
    boolean legacy = false;

    ExtendedIcon icon = event.getExtendedIcon();
    ConfigurationSection section = event.getConfigurationSection();

    Requirements requirements = icon.getRequirements();

    if (section.isSet(Nodes.PRICE)) {
      legacy = true;
      String value = section.getString(Nodes.PRICE, "0");
      MoneyIconRequirement requirement = new MoneyIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.POINTS)) {
      legacy = true;
      String value = section.getString(Nodes.POINTS, "0");
      PointIconRequirement requirement = new PointIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.TOKENS)) {
      legacy = true;
      String value = section.getString(Nodes.TOKENS, "0");
      TokenIconRequirement requirement = new TokenIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.EXP_LEVELS)) {
      legacy = true;
      String value = section.getString(Nodes.EXP_LEVELS, "0");
      ExpLevelIconRequirement requirement = new ExpLevelIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isConfigurationSection(Nodes.REQUIRED_ITEM)) {
      legacy = true;
      for (ClickType type : ClickType.values()) {
        String subsection = Nodes.REQUIRED_ITEM + "." + type.name();
        if (section.isSet(subsection)) {
          String value = section.getString(subsection);
          ItemIconRequirement requirement = new ItemIconRequirement();
          requirement.setValue(value);
          requirements.addClickRequirement(requirement, type);
        }
      }
      if (section.isSet(Nodes.REQUIRED_ITEM_DEFAULT)) {
        String value = section.getString(Nodes.REQUIRED_ITEM_DEFAULT);
        ItemIconRequirement requirement = new ItemIconRequirement();
        requirement.setValue(value);
        requirements.addDefaultClickRequirement(requirement);
      }
    } else if (section.isSet(Nodes.REQUIRED_ITEM)) {
      legacy = true;
      String value = section.getString(Nodes.REQUIRED_ITEM);
      ItemIconRequirement requirement = new ItemIconRequirement();
      requirement.setValue(value);
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.PERMISSION)) {
      legacy = true;
      String value = section.getString(Nodes.PERMISSION);
      PermissionIconRequirement requirement = new PermissionIconRequirement();
      requirement.setValue(value);
      if (section.isSet(Nodes.PERMISSION_MESSAGE)) {
        requirement.setFailMessage(section.getString(Nodes.PERMISSION_MESSAGE));
      }
      requirements.addDefaultClickRequirement(requirement);
    }

    if (section.isSet(Nodes.VIEW_PERMISSION)) {
      legacy = true;
      String value = section.getString(Nodes.VIEW_PERMISSION);
      PermissionIconRequirement requirement = new PermissionIconRequirement();
      requirement.setValue(value);
      requirements.addViewRequirement(requirement);
    }

    if (section.isSet(Nodes.VIEW_REQUIREMENT) && !section
        .isConfigurationSection(Nodes.VIEW_REQUIREMENT)) {
      legacy = true;
      String value = section.getString(Nodes.VIEW_REQUIREMENT);
      ConditionIconRequirement requirement = new ConditionIconRequirement();
      requirement.setValue(value);
      requirements.addViewRequirement(requirement);
    }

    if (section.isSet(Nodes.CLICK_REQUIREMENT) && !section
        .isConfigurationSection(Nodes.CLICK_REQUIREMENT)) {
      legacy = true;
      String value = section.getString(Nodes.CLICK_REQUIREMENT);
      ConditionIconRequirement requirement = new ConditionIconRequirement();
      requirement.setValue(value);
      if (section.isSet(Nodes.CLICK_REQUIREMENT_MESSAGE)) {
        requirement.setFailMessage(section.getString(Nodes.CLICK_REQUIREMENT_MESSAGE));
      }
      requirements.addDefaultClickRequirement(requirement);
    }

    if (legacy) {
      event.getErrorLogger().addWarning(
          "The icon \"" + event.getIconName() + "\" in the menu \"" + event.getMenuFileName()
              + "\" used the old settings. It's recommended to update to the new settings");
    }
  }

  private static class Nodes {

    static final String PRICE = "PRICE";
    static final String POINTS = "POINTS";
    static final String TOKENS = "TOKENS";
    static final String EXP_LEVELS = "LEVELS";
    static final String REQUIRED_ITEM = "REQUIRED-ITEM";
    static final String REQUIRED_ITEM_DEFAULT = "REQUIRED-ITEM.DEFAULT";
    static final String PERMISSION = "PERMISSION";
    static final String PERMISSION_MESSAGE = "PERMISSION-MESSAGE";
    static final String VIEW_PERMISSION = "VIEW-PERMISSION";
    static final String VIEW_REQUIREMENT = "VIEW-REQUIREMENT";
    static final String CLICK_REQUIREMENT = "CLICK-REQUIREMENT";
    static final String CLICK_REQUIREMENT_MESSAGE = "CLICK-REQUIREMENT-MESSAGE";
  }
}
