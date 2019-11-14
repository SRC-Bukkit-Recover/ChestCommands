package com.gmail.filoghost.chestcommands.util.nbt;

/**
 * The {@code TAG_Int} tag.
 */
public final class NBTInt extends NBTTag {

  private int value;

  public NBTInt(int value) {
    this.value = value;
  }

  public NBTInt(NBTInt source) {
    this.value = source.value;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  public int getIntValue() {
    return value;
  }

  public void setIntValue(int value) {
    this.value = value;
  }

  @Override
  public NBTType getType() {
    return NBTType.INT;
  }

  // MISC

  @Override
  public boolean equals(Object obj) {
    return obj instanceof NBTInt && equals((NBTInt) obj);
  }

  public boolean equals(NBTInt tag) {
    return this.value == tag.value;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }

  @Override
  public String toMSONString() {
    return Integer.toString(value);
  }

}
