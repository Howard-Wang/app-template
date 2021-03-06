# 介绍

[![MIT](https://img.shields.io/dub/l/vibe-d.svg?style=flat-square)](http://opensource.org/licenses/MIT)

![海报](./poster.jpg)

[English](./README.en.md)

app-template 是基于 Spring Boot 和 Vue 的快速开发模板

演示

* [Web 管理端](http://47.105.79.88:9090/app-template/admin/index.html)
* [Web 手机端](http://47.105.79.88:9090/app-template/index.html)

项目采用前后端分离模式，对前后端开发中常用的技术模块进行了整合，并对一些常用功能进行
了实现，主要是为了方便 Web 程序的的开发.

整个项目有三端组成：**后端**，**管理端**，**手机 web 端**，并且打包到一个 **war** 包中方便部署

具体整合的技术有：

* Spring Boot
* Vue
* Mybatis-plus
* Swagger
* Spring security + jwt
* 美团的 [Logan](https://github.com/Meituan-Dianping/Logan)
* Gradle

详细说明请参考 `doc\manual` 下的文档

* [项目的详细配置和编译说明](./doc/manual/项目配置.md)
* [用户认证和授权](./doc/manual/用户认证和授权(用户名和密码).md)
* [Web 端权限管理](./doc/manual/权限管理.md)
* [后端日志处理](./doc/manual/日志.md)
* [国际化](./doc/manual/国际化.md)
* [Logan 的使用](./doc/manual/logan.md)
* [Swagger 的使用](./doc/manual/swagger.md)
* [Http 和 Https 的配置](./doc/manual/http和https.md)
* [H2 数据库的使用](./doc/manual/h2数据库.md)

实现的功能有：

* 开发环境与生成环境的配置切换
* 用户登录登出，并使用 spring boot + jwt 进行授权认证
* 用户权限控制
* 项目运行日志的在线查看与下载
* 项目日志输出级别的动态切换
* 客户端日志的上报、查看与下载
* 前后端国际化处理
* Swagger 在线文档的编辑展示
* 通过 Logan 来获取前端出错日志

## 1. 编译

**注意**: 如果是编译 **prod** 版本并且想在 `build\libs` 目录下直接运行，则请先
在 `resources/application-pro.yml` 中设置正确的 Mysql 用户名、密码、数据库名称，
并且在运行前确保数据库初始化完毕。如果是在 deploy 目录下通过 `start.sh` 来运行，则
可以在运行前再配置 `app/application-pro.yml`。

编译说明：

* 在 Linux 中，执行 `./build.sh` 然后根据提示选择编译对应模式的版本, 编译完成后会
将整个前端和后端，以及 `deploy` 目录下的素材打包成一个包(**app-deploy.tgz**)供发布
使用
* 在 Windows 中，执行 `build.bat` 然后根据提示选择编译对应模式的版本, 编译完成后会在
`build\libs` 目录下生成 war 包, 可以通过 `java -jar [war 包]` 来运行

编译支持**开发版本**和**生产版本**，两者唯一的区别就是**数据库**使用的不一样，
开发模式下默认使用的是 **H2** 数据库，生产模式使用的是 **Mysql** 数据库

建议：如果只是想看看效果可以使用开发版本，这样可以省去配置 Mysql 的步骤

如果想对前后端分别编译，可以参考 [项目的详细配置和编译说明](./doc/manual/项目配置.md)
或者通过查看编译脚本来了解细节

## 2. 运行

有两种运行方式

* 在 `build\libs` 目录下直接运行，通常这是开发调试的时候使用
* 在通过部署包 **app-deploy.tgz** 解压后安装来运行，这是生产模式下会使用

**注意**: 两种运行方式衣依赖的 `application-pro.yml` 文件是不一样的，项目中该文件有两个

* src/main/resources/application-pro.yml
* deploy/app/application-pro.yml

如果在 deploy 目录下通过 `start.sh` 来运行则需要修改的是 deploy 目录下的
`application-pro.yml` 中的配置

如果在 `build/libs` 目录下直接运行 war 包，则需要在 build 前修改 resources 目录下的
`application-pro.yml` 中的配置

### 2.1 初始化数据库

在生产模式下，运行之前**必须** 初始化 **Mysql** 数据库。如果编译的是开发模式则不需要。

在 Mysql 中创建好数据库之后，可以通过导入 `deploy\init.sql` 来进行初始化
或者执行安装程序 `install.sh` 来初始化，关于 `install.sh` 的使用说明
可以参考 `deploy/README.md`

在部署的目录下执行

```sh
./start.sh
```

服务器启动之后，访问网址

* 手机端网页 : http://ip:port/app-template/index.html
* 管理端网页 : http://ip:port/app-template/admin/index.html
* swagger ui : http://ip:port/app-template/swagger-ui.html

## 3. 其他说明

### 3.1 数据库初始化

因为 **H2** 和 **Mysql** 的差异性，所以两者的初始化 sql 脚本不一样

在 `resources/db/` 目录下是 **H2** 数据库初始化使用的

* `schema-h2.sql` 数据库结构
* `data-h2.sql` 数据库数据

在 `deploy/init.sql` 是 **Mysql** 数据库初始化使用

### 3.2 为什么使用 context-path

`server.servlet.context-path=app-template`, 应用的上下文路径，也可以称为项目路径，
是构成 url 地址的一部分。

加上该配置后，会自动在 url 前加上 `/app-template`, 如：原来的接口为
 `localhost:8080/api/user`, 加上该配置后就会
 变成 `localhost:8080/app-template/api/user`

该配置在 ngnix 分发中起作用，主要功能是将所有请求的 url 中包含 `/app-template` 的
请求，分发到指定的 server。

如果不使用 nginx 来进行转发，则可以将该配置删除。
**注意** : 在删除的同时，需要将前端代码中 url 的 `app-template` 也删除。
