package org.selins.configuration

import java.util
import java.util.concurrent.ConcurrentHashMap
import java.util.{Objects, Properties}

import scala.collection.JavaConversions._

/**
 * @title: Configuration
 * @projectName easy-configuration
 * @description: TODO
 * @author 赵云海
 * @date 2020/5/9 14:25
 */
class Configuration() extends Serializable with Cloneable {
  /** Listener list. */
  var listeners = new util.ArrayList[ConfigChangeListener]()
  /** Stores the concrete key/value pairs of this configuration object. */
  var originals = new ConcurrentHashMap[String, Any]()

  /**
   * Creates a new configuration with the copy of the given configuration.
   *
   * param other The configuration to copy the entries from.
   */
  def this(other: Configuration) {
    this()
    this.originals.putAll(other.originals)
  }

  /**
   * Returns the value associated with the given key as a string.
   *
   * param key
   * the key pointing to the associated value
   * param defaultValue
   * the default value which is returned in case there is no value associated with the given key
   * return the (default) value associated with the given key
   */
  def getValue[T](
    key: String)
  : T = {
    getValueInternal(key).asInstanceOf[T]
  }

  /**
   * Returns the value associated with the given key as a string.
   *
   * param key
   * the key pointing to the associated value
   * param defaultValue
   * the default value which is returned in case there is no value associated with the given key
   * return the (default) value associated with the given key
   */
  def getValue[T](
    key: String,
    defaultValue: T)
  : T = {
    val o = getValueInternal(key)
    if (o == null)
      defaultValue
    else
      o.asInstanceOf[T]
  }

  /**
   * Returns the value associated with the given config option as a string.
   *
   * param configOption The configuration option
   * return the (default) value associated with the given config option
   */
  def getValue[T](configOption: Option[T]): T = {
    val o = getValueInternal(configOption)
    if (o == null)
      configOption.defaultValue()
    else
      o.asInstanceOf[T]
  }

  /**
   * Adds the given key/value pair to the configuration object.
   *
   * param key
   * the key of the key/value pair to be added
   * param value
   * the value of the key/value pair to be added
   */
  def setValue[T](
    key: String,
    value: T): T = {
    setValueInternal(key, value)
    value
  }

  /**
   * Adds the given value to the configuration object.
   * The main key of the config option will be used to map the value.
   *
   * param key
   * the option specifying the key to be added
   * param value
   * the value of the key/value pair to be added
   */
  def setValue[T](
    key: Option[T],
    value: T): T = {
    setValueInternal(key.key, value)
    value
  }

  /**
   * Adds the given key/value pair to the configuration object. The class can be retrieved by invoking
   * {@link #getClass(String, Class, ClassLoader)} if it is in the scope of the class loader on the caller.
   *
   * @param key   The key of the pair to be added
   * @param clazz The value of the pair to be added
   * @see #getClass(String, Class, ClassLoader)
   */
  def setClass[T](
    key: String,
    clazz: Class[_ <: T]): Class[T] = {
    setValueInternal(key, clazz.getName)
    clazz.asInstanceOf[Class[T]]
  }

  /**
   * Adds the given key/value pair to the configuration object. The class can be retrieved by invoking
   * {@link #getClass(String, Class, ClassLoader)} if it is in the scope of the class loader on the caller.
   *
   * @param key   The key of the pair to be added
   * @param clazz The value of the pair to be added
   * @see #getClass(String, Class, ClassLoader)
   */
  def setClass[T](
    key: Option[T],
    clazz: Class[_ <: T]): Class[T] = {
    setValueInternal(key.key, clazz)
    clazz.asInstanceOf[Class[T]]
  }

  /**
   * Returns the class associated with the given key as a string.
   *
   * @param configOption The key pointing to the associated value
   * @param classLoader  The class loader used to resolve the class.
   * @return The value associated with the given key, or the default value, if to entry for the key exists.
   */
  @SuppressWarnings(Array("unchecked"))
  @throws[ClassNotFoundException]
  def getClass[T](
    configOption: Option[Class[_ <: T]],
    classLoader: ClassLoader)
  : Class[T] = {
    getClass[T](configOption.key(), configOption.defaultValue(), classLoader)
  }

  /**
   * Returns the class associated with the given key as a string.
   *
   * @param key          The key pointing to the associated value
   * @param defaultValue The optional default value returned if no entry exists
   * @param classLoader  The class loader used to resolve the class.
   * @return The value associated with the given key, or the default value, if to entry for the key exists.
   */
  @SuppressWarnings(Array("unchecked"))
  @throws[ClassNotFoundException]
  def getClass[T](
    key: String,
    defaultValue: Class[_ <: T],
    classLoader: ClassLoader)
  : Class[T] = {
    val o = getValueInternal(key)
    if (o == null || (o.getClass ne classOf[String]))
      defaultValue.asInstanceOf[Class[T]]
    else
      Class.forName(o.asInstanceOf[String], true, classLoader).asInstanceOf[Class[T]]
  }

  /**
   * Get the original storage information
   *
   * @param key
   * @return
   */
  private def getValueInternal(
    key: String)
  : Any
  = {
    Objects.requireNonNull(key, "Key must not be null.")
    this.originals.get(key)
  }

  /**
   * Get the original storage information
   *
   * @param configOption
   * @return
   */
  private def getValueInternal(
    configOption: Option[_])
  : Any = {
    val o = getValueInternal(configOption.key)
    if (o != null) {
      return o
    }
    getDeprecatedEntry(configOption)
  }


  /**
   * Get the original storage information
   *
   * @param configOption
   * @return
   */
  private def getDeprecatedEntry(
    configOption: Option[_])
  : Any = {
    configOption.deprecatedKeys
      .find(containsKey(_))
      .map(getValueInternal(_)) getOrElse null
  }

  /**
   * Set the original storage information
   *
   * @param key
   * @param value
   * @return
   */
  private def setValueInternal[T](
    key: String,
    value: T)
  : Unit = {
    Objects.requireNonNull(key, "Key must not be null.")
    val old = this.originals put(key, value)
    notify(key, value, old) //通知更新消息
  }

  /**
   * Checks whether there is an entry with the specified key.
   *
   * param key key of entry
   * return true if the key is stored, false otherwise
   */
  def containsKey(key: String): Boolean = {
    this.originals containsKey key
  }


  /**
   * Returns the keys of all key/value pairs stored inside this
   * configuration object.
   *
   * return the keys of all key/value pairs stored inside this configuration object
   */
  def keySet: util.Set[String] = {
    this.originals.keySet()
  }


  /**
   * Add another identical profile information
   *
   * @param other
   */
  def addAll(other: Configuration): Unit = {
    this.originals.putAll(other.originals)
  }


  /**
   * Adds all entries in this {link Properties} to the given {code Configuration}.
   */
  def load(props: Properties): Unit = {
    for (entry <- props.entrySet()) {
      originals.put(entry.getKey.asInstanceOf[String], entry.getValue)
    }
  }

  /**
   * Adds all entries in this {link Properties} to the given {code Configuration}.
   */
  def load(map: util.Map[String, Any]): Unit = {
    originals.putAll(map)
  }

  /**
   * Add a listener
   *
   * @param listener
   */
  def addChangeListener(
    listener: ConfigChangeListener)
  : Unit = {
    listeners.add(listener)
  }

  /**
   * Remove a listener
   *
   * @param listener
   */
  def removeChangeListener(
    listener: ConfigChangeListener)
  : Unit = {
    listeners.remove(listener)
  }

  /**
   * Notify all listeners
   */
  def notify[T](
    key: String,
    newValue: T,
    oldValue: T)
  : Unit = {
    for (e <- listeners) {
      e.onChange(key, newValue, oldValue)
    }
  }

  /**
   * Clone the content information saved in the current configuration file
   *
   * @return
   */
  override def clone: Configuration = {
    val config = new Configuration
    config.addAll(this)
    config
  }

  /**
   * Calculation of hashcode
   *
   * @return
   */
  override def hashCode: Int = {
    var hash = 0
    for (s <- this.originals.keySet) {
      hash ^= s.hashCode
    }
    hash
  }

  /**
   * Convert to a string
   *
   * @return
   */
  override def toString: String = this.originals.toString
}
