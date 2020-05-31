# 介绍

部署安装说明，安装部署可以手动进行，也可以通过 `install.sh` 脚本来进行

**注意**：如果使用了 **nginx** 则相关配置需要手动执行。

**注意**: 在 `app/application-pro.yml` 文件名不能修改，因为项目中 Mysql 的配置
是在该文件中，所以如果是使用其他名称则 Mysql 相关的配置不会生效

## 初始安装

1、拷贝配置文件

```sh
cp app_conf_sample.sh app_conf.sh
```

2、修改配置文件 `app_conf.sh` 配置项

详见下方**配置说明**

3、执行安装脚本

```sh
sh install.sh install
```

4、配置 Nginx(如果没有则可以省略)

```sh
location ~* ^/app-template {
    proxy_pass  http://127.0.0.1:8090;
}
```

* **app-template** 是在 **context-path** 中配置的值
* **8090** 程序绑定的端口号, 可以在 `app_conf.sh` 中通过 **server_port** 来配置

5、启动程序

部署后的目录下执行

```sh
sh start.sh
```

## 配置说明

* install_path 程序部署路径
* log_path 日志存放路径
* server_port 服务启动端口
* db_host 数据库服务器ip地址
* db_port 数据库服务端口
* db_dbname 数据库 database 名称
* db_username 数据库登录用户名
* db_password 数据库登录密码
