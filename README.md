# easy-configuration
这是一个配置文件工具, 可以进行配置文件的设置, 来源于flink源码, 并进行简化处理。

# 使用方法
##引用依赖
    <repository>
        <id>mvn-repo</id>
        <url>https://github.com/zhaoyunhai1216/mvn-repo/master</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
    
    <dependency>
        <groupId>org.selins</groupId>
        <artifactId>easy-configuration</artifactId>
        <version>0.1.0</version>
    </dependency>

    #如果以上引用不好用,可以自行下载编译
##调用方法

    public static final Option<String> HA_ZOOKEEPER_QUORUM =
        key("high-availability.zookeeper.quorum")
            .noDefaultValue()
            .withDeprecatedKeys("recovery.zookeeper.quorum")
            .withDescription("The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper.");
    
    Configuration configuration = new Configuration();
    //设置监听,当有变化的时候进行通知
    configuration.addChangeListener((x, y, z) -> {
        System.out.println(x + "," + y + "," + z);
    });
    
    //正常获取(字符串方式)
    configuration.setValue("testKey", "testValue");
    String s = configuration.getValue(testKey);
        
    //正常获取
    configuration.setValue(HA_ZOOKEEPER_QUORUM, "127.0.0.1:2181");
    String s = configuration.getValue(HA_ZOOKEEPER_QUORUM);
    
    //过期key匹配获取
    configuration.setValue("recovery.zookeeper.quorum", "127.0.0.1:2181");
    String s = configuration.getValue(HA_ZOOKEEPER_QUORUM);