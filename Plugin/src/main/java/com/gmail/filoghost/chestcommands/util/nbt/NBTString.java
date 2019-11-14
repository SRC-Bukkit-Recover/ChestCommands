package com.gmail.filoghost.chestcommands.util.nbt;

/**
 * The {@code TAG_String} tag.
 */
public final class NBTString extends NBTTag implements Cloneable {

  private String value;

  public NBTString(String value) {
    setValue(value);
  }

  /**
   * Converts a regular string into a Mojangson string by surrounding it with quotes and escaping
   * backslashes and quotes inside it.
   *
   * @param str the string
   * @return the Mojangson string
   */
  public static String toMSONString(String str) {
    StringBuilder builder = new StringBuilder("\"");
    char[] chars = str.toCharArray();
    for (char c : chars) {
      if ((c == '\\') || (c == '"')) {
        builder.append('\\');
      }
      builder.append(c);
    }
    return builder.append('\"').toString();
  }

  @Override
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  // MISC

  @Override
  public NBTType getType() {
    return NBTType.STRING;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toMSONString() {
    return toMSONString(value);
  }

  // UTIL

  @Override
  public NBTString clone() {
    return new NBTString(value);
  }

}
