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
package com.gmail.filoghost.chestcommands.serializer;

import com.gmail.filoghost.chestcommands.api.IconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ConditionIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ExpLevelIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.ItemIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.MoneyIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.PermissionIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.PointIconRequirement;
import com.gmail.filoghost.chestcommands.internal.requirement.requirement.TokenIconRequirement;
import com.gmail.filoghost.chestcommands.util.ErrorLogger;
import com.gmail.filoghost.chestcommands.util.FormatUtils;
import com.gmail.filoghost.chestcommands.util.Utils;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class RequirementSerializer {

  private static Map<String, Class<? extends IconRequirement>> requirementTypesMap = Utils
      .newHashMap();

  static {
    register("LEVEL", ExpLevelIconRequirement.class);
    register("MONEY", MoneyIconRequirement.class);
    register("PERMISSION", PermissionIconRequirement.class);
    register("POINT", PointIconRequirement.class);
    register("TOKEN", TokenIconRequirement.class);
    register("CONDITION", ConditionIconRequirement.class);
    register("ITEM", ItemIconRequirement.class);
  }

  private RequirementSerializer() {

  }

  public static void checkClassConstructors(ErrorLogger errorLogger) {
    for (Class<? extends IconRequirement> clazz : requirementTypesMap.values()) {
      try {
        clazz.getDeclaredConstructor().newInstance();
      } catch (Exception ex) {
        String className = clazz.getName().replace("Requirement", "");
        className = className.substring(className.lastIndexOf('.') + 1);
        errorLogger.addError(
            "Unable to register the \"" + className + "\" requirement type(" + ex.getClass()
                .getName()
                + "), please inform the developer.");
      }
    }
  }

  static List<IconRequirement> loadRequirementsFromSection(ConfigurationSection section,
      String iconName, String menuFileName, ErrorLogger errorLogger) {
    List<IconRequirement> requirements = Utils.newArrayList();

    for (String requirementType : requirementTypesMap.keySet()) {
      if (section.isConfigurationSection(requirementType)) {
        IconRequirement requirement = getRequirement(requirementType);
        if (requirement != null) {
          if (section.isSet(requirementType + ".VALUE")) {
            requirement.setValue(section.getString(requirementType + ".VALUE"));
            requirement.setFailMessage(
                FormatUtils.addColors(section.getString(requirementType + ".MESSAGE")));
            requirement.canTake(section.getBoolean(requirementType + ".TAKE", true));
          } else {
            errorLogger.addError(
                "The requirement \"" + requirementType + "\" in the icon \"" + iconName
                    + "\" in the menu \""
                    + menuFileName + "\" doesn't have VALUE");
            continue;
          }
          requirements.add(requirement);
        }
      } else if (section.isSet(requirementType)) {
        IconRequirement requirement = getRequirement(requirementType);
        if (requirement != null) {
          requirement.setValue(section.getString(requirementType));
          requirements.add(requirement);
        }
      }
    }

    return requirements;
  }

  private static IconRequirement getRequirement(String type) {
    Class<? extends IconRequirement> clazz = requirementTypesMap.get(type);
    if (clazz != null) {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        // Checked at startup
      }
    }

    return null;
  }

  public static void register(String type, Class<? extends IconRequirement> clazz) {
    requirementTypesMap.put(type, clazz);
  }
}
