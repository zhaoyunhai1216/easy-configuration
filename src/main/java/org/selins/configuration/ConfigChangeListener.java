package org.selins.configuration;

/**
 * @title: ConfigChangeListener
 * @projectName easy-configuration
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/5/11 19:44
 */
public interface ConfigChangeListener {
    void onChange(String key, Object newValue, Object oldValue);
}
