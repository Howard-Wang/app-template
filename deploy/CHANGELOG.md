# 更新说明

该文件主要用来记录不同版本之间的详细改动

手动替换

* 替换: app-xxx.war
* 替换: application-pro.yml, 如果不是特殊声明通常**不需要替换该文件**

**注意**: 因为在 `start.sh` 中使用的是 **spring.config.additional-location**
所以在 `application-pro.yml` 中只需要写入**需要改变**的配置项，没有写的配置项将使用
程序的默认设置
