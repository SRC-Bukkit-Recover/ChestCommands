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
package com.gmail.filoghost.chestcommands.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Utils {

  private Utils() {
  }

  public static int makePositive(int i) {
    return Math.max(i, 0);
  }

  public static boolean isValidInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidPositiveInteger(String input) {
    try {
      return Integer.parseInt(input) > 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidShort(String input) {
    try {
      Short.parseShort(input);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidPositiveDouble(String input) {
    try {
      return Double.parseDouble(input) > 0.0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static List<String> readLines(File file) throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      List<String> lines = newArrayList();

      String line = br.readLine();

      while (line != null) {
        lines.add(line);
        line = br.readLine();
      }

      return lines;
    }
  }

  public static <T> Set<T> newHashSet() {
    return new HashSet<>();
  }

  public static <T, V> Map<T, V> newHashMap() {
    return new HashMap<>();
  }

  public static <T> List<T> newArrayList() {
    return new ArrayList<>();
  }

  public static boolean isClassLoaded(String name) {
    try {
      Class.forName(name);
      return true;
    } catch (Exception t) {
      return false;
    }
  }

  public static boolean isNullOrEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isValidURL(String input) {
    try {
      new URL(input).toURI();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
