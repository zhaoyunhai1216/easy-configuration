package org.selins.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * A {@code ConfigOption} describes a configuration parameter. It encapsulates
 * the configuration key, deprecated older versions of the key, and an optional
 * default value for the configuration parameter.
 *
 * <p>{@code ConfigOptions} are built via the {@link Options} class.
 * Once created, a config option is immutable.
 *
 * @param <T> The type of value associated with the configuration option.
 */
public class Option<T> {

    private static final String[] EMPTY = new String[0];

    // ------------------------------------------------------------------------

    /**
     * The current key for that config option.
     */
    private final String key;

    /**
     * The list of deprecated keys, in the order to be checked.
     */
    private final String[] deprecatedKeys;

    /**
     * The default value for this option.
     */
    private final T defaultValue;

    /**
     * The description for this option.
     */
    private final String description;

    // ------------------------------------------------------------------------

    /**
     * Creates a new config option with no deprecated keys.
     *
     * @param key          The current key for that config option
     * @param defaultValue The default value for this option
     */
    Option(String key, T defaultValue) {
        this.key = Objects.requireNonNull(key);
        this.description = "";
        this.defaultValue = defaultValue;
        this.deprecatedKeys = EMPTY;
    }

    /**
     * Creates a new config option with deprecated keys.
     *
     * @param key            The current key for that config option
     * @param description    Description for that option
     * @param defaultValue   The default value for this option
     * @param deprecatedKeys The list of deprecated keys, in the order to be checked
     */
    @Deprecated
    Option(String key, String description, T defaultValue, String... deprecatedKeys) {
        this.key = Objects.requireNonNull(key);
        this.description = description;
        this.defaultValue = defaultValue;
        this.deprecatedKeys = deprecatedKeys == null || deprecatedKeys.length == 0 ? EMPTY : deprecatedKeys;
    }


    // ------------------------------------------------------------------------

    /**
     * Creates a new config option, using this option's key and default value, and
     * adding the given deprecated keys.
     *
     * <p>When obtaining a value from the configuration via
     * the deprecated keys will be checked in the order provided to this method. The first key for which
     * a value is found will be used - that value will be returned.
     *
     * @param deprecatedKeys The deprecated keys, in the order in which they should be checked.
     * @return A new config options, with the given deprecated keys.
     */
    public Option<T> withDeprecatedKeys(String... deprecatedKeys) {
        return new Option<>(key, description, defaultValue, deprecatedKeys);
    }

    /**
     * Creates a new config option, using this option's key and default value, and
     * adding the given description. The given description is used when generation the configuration documention.
     *
     * @param description The description for this option.
     * @return A new config option, with given description.
     */
    public Option<T> withDescription(final String description) {
        return new Option<>(key, description, defaultValue, deprecatedKeys);
    }

    // ------------------------------------------------------------------------

    /**
     * Gets the configuration key.
     *
     * @return The configuration key
     */
    public String key() {
        return key;
    }

    /**
     * Checks if this option has a default value.
     *
     * @return True if it has a default value, false if not.
     */
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    /**
     * Returns the default value, or null, if there is no default value.
     *
     * @return The default value, or null.
     */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Checks whether this option has deprecated keys.
     *
     * @return True if the option has deprecated keys, false if not.
     */
    public boolean hasDeprecatedKeys() {
        return deprecatedKeys != EMPTY;
    }

    /**
     * Gets the deprecated keys, in the order to be checked.
     *
     * @return The option's deprecated keys.
     */
    public Iterable<String> deprecatedKeys() {
        return deprecatedKeys == EMPTY ? Collections.<String>emptyList() : Arrays.asList(deprecatedKeys);
    }

    /**
     * Returns the description of this option.
     *
     * @return The option's description.
     */
    public String description() {
        return description;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && o.getClass() == Option.class) {
            Option<?> that = (Option<?>) o;
            return this.key.equals(that.key) &&
                    Arrays.equals(this.deprecatedKeys, that.deprecatedKeys) &&
                    (this.defaultValue == null ? that.defaultValue == null :
                            (that.defaultValue != null && this.defaultValue.equals(that.defaultValue)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * key.hashCode() +
                17 * Arrays.hashCode(deprecatedKeys) +
                (defaultValue != null ? defaultValue.hashCode() : 0);
    }

    @Override
    public String toString() {
        return String.format("Key: '%s' , default: %s (deprecated keys: %s)",
                key, defaultValue, Arrays.toString(deprecatedKeys));
    }
}

