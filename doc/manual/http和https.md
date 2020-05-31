# 介绍

程序支持通过 Http 和 Https 的方式来访问

默认情况下是只支持Http，如果要支持Https，需要修改如下两个文件

## 1. 修改 application.yml

```java
server:
    # HTTPS 端口
    port: 8443
    ssl:
        key-store: classpath:keystore.p12
        key-store-password: 123qwe
        keyStoreType: PKCS12

customize:
    # HTTP 端口
    http:
        port: 8090
```

## 2. 修改 TomcatConfig

修改 TomcatConfig，将 **@Configuration** 放开

```java
@Configuration
public class TomcatConfig {
}
```

一旦这么做之后，可以直接访问 https，同时如果是 http 访问也会自动的转为 https