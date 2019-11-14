package com.gmail.filoghost.chestcommands.util.nbt;

/**
 * The {@code TAG_Short} tag.
 */
public final class NBTShort extends NBTTag {

  private short value;

  public NBTShort(short value) {
    this.value = value;
  }

  public NBTShort(NBTShort source) {
    this.value = source.value;
  }

  @Override
  public Short getValue() {
    return value;
  }

  public short getShortValue() {
    return value;
  }

  public void setShortValue(short value) {
    this.value = value;
  }

  @Override
  public NBTType getType() {
    return NBTType.SHORT;
  }

  // MISC

  @Override
  public boolean equals(Object obj) {
    return obj instanceof NBTShort && equals((NBTShort) obj);
  }

  public boolean equals(NBTShort tag) {
    return this.value == tag.value;
  }

  @Override
  public int hashCode() {
    return Short.hashCode(value);
  }

  @Override
  public String toMSONString() {
    return value + "s";
  }

}
