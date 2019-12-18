package com.gmail.filoghost.chestcommands.internal.requirement;

import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Requirements {

  private List<IconRequirement> viewRequirements = Utils.newArrayList();
  private final Map<ClickType, List<IconRequirement>> perTypeClickRequirements = Utils.newHashMap();
  private List<IconRequirement> defaultClickRequirements = Utils.newArrayList();

  public void addClickRequirement(IconRequirement requirement, ClickType type) {
    if (!perTypeClickRequirements.containsKey(type)) {
      perTypeClickRequirements.put(type, defaultClickRequirements);
    }
    this.perTypeClickRequirements.get(type).add(requirement);
  }

  public void addViewRequirement(IconRequirement requirement) {
    this.viewRequirements.add(requirement);
  }

  public void addDefaultClickRequirement(IconRequirement requirement) {
    this.defaultClickRequirements.add(requirement);
  }

  public void addClickRequirements(List<IconRequirement> requirements, ClickType type) {
    this.perTypeClickRequirements.put(type, requirements);
  }

  public void addViewRequirements(List<IconRequirement> requirements) {
    this.viewRequirements = requirements;
  }

  public void addDefaultClickRequirements(List<IconRequirement> requirements) {
    this.defaultClickRequirements = requirements;
  }

  public boolean canSee(Player player) {
    for (IconRequirement requirement : viewRequirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public boolean check(Player player, ClickType type) {
    List<IconRequirement> requirements = perTypeClickRequirements
        .getOrDefault(type, defaultClickRequirements);
    for (IconRequirement requirement : requirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public void take(Player player, ClickType type) {
    List<IconRequirement> requirements = perTypeClickRequirements
        .getOrDefault(type, defaultClickRequirements);
    for (IconRequirement requirement : requirements) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }

  public void takeView(Player player) {
    for (IconRequirement requirement : viewRequirements) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }

  public boolean hasViewRequirement() {
    return !viewRequirements.isEmpty();
  }
}
