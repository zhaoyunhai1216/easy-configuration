package org.selins.configuration

import java.util.Objects

/**
 * @title: OptionBuilder
 * @projectName easy-configuration
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/5/9 24:36
 */
class OptionBuilder(key: String) {
  /**
   * Creates a ConfigOption with the given default value.
   *
   * <p>This method does not accept "null". For options with no default value, choose
   * one of the {@code noDefaultValue} methods.
   *
   * @param value The default value for the config option
   * @return The config option with the default value.
   */
  def defaultValue[T](value: T): Option[T] = {
    Objects.requireNonNull(value,"the default value cannot be null.")
    new Option[T](key, value)
  }

  /**
   * Creates a string-valued option with no default value.
   * String-valued options are the only ones that can have no
   * default value.
   *
   * @return The created ConfigOption.
   */
  def noDefaultValue = new Option[String](key, null)
}
