package org.selins.configuration

import java.util.Objects

/**
 * @title: Options
 * @projectName easy-configuration
 * @description: TODO
 * @author qdyk
 * @date 2020/5/914:35
 */
object Options {
  /**
   * Starts building a new {@link Option}.
   *
   * @param key The key for the config option.
   * @return The builder for the config option with the given key.
   */
  def key(key: String): OptionBuilder = {
    Objects.requireNonNull(key, "the key of the configuration cannot be null.")
    new OptionBuilder(key)
  }
}
