package org.selins.configuration;

import static org.selins.configuration.Options.key;

/**
 * @author qdyk
 * @title: Test
 * @projectName easy-configuration
 * @description: TODO
 * @date 2020/5/917:41
 */
public class Test {
    /**
     * The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper.
     */
    public static final Option<String> HA_ZOOKEEPER_QUORUM =
            key("high-availability.zookeeper.quorum")
                    .noDefaultValue()
                    .withDeprecatedKeys("recovery.zookeeper.quorum")
                    .withDescription("The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper.");

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.addChangeListener((x, y, z) -> {
            System.out.println(x + "," + y + "," + z);
        });
        configuration.setValue("recovery.zookeeper.quorum", "127.0.0.1:2181");
        String s = configuration.getValue(HA_ZOOKEEPER_QUORUM);
        System.out.println(s);
    }
}
