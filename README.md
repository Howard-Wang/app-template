
# Introduction

[![MIT](https://img.shields.io/dub/l/vibe-d.svg?style=flat-square)](http://opensource.org/licenses/MIT)

![poster](./poster.jpg)

[简体中文](./README.zh-CN.md)

**App-template** is a rapid development template based on Spring Boot and Vue.
The project adopts a front-end and back-end separation model, integrates the
commonly used technical modules in front-end and back-end development,
and implements some common functions, which greatly facilitates the development
of enterprises and individuals.

The whole project consists of three ends: **back end**, **management web**,
**mobile web** end, and packaged into a **war** package for easy deployment.

The specific integrated technologies are:
* Spring Boot
* Vue
* Mybatis-plus
* Swagger
* Spring security + jwt
* [Logan](https://github.com/Meituan-Dianping/Logan) of Meituan-Dianping
* Gradle

For detailed instructions, please refer to the documentation under `doc\manual`(Chinese Only)

The functions implemented are:
* Configuration switch between development environment and build environment
* User login and logout, and use spring boot + jwt for authorization and authentication
* User permission control
* Online viewing and downloading of project operation logs
* Dynamic switching of project log output level Reporting, viewing and downloading of client logs
* Internationalization of front and back ends
* Swagger online document editing display
* Use Logan to get the front-end error log

## Compile

* Linux : Run `./build.sh` Then choose the compiled version according to the prompt.
* Windows : Run `build.bat` Then choose the compiled version according to the prompt.

Compilation supports the **development mode** and the **production mode**.
The only difference between the two is that the database is not the same.
The H2 database is used by default in development mode, and the Mysql database
is used in production mode. After the compilation is complete,
the entire front-end and back-end, and the materials under the deploy directory
 will be packaged into a package for publishing If you want to compile the
 front and back end separately, you can refer to the instructions below

### Back-end compile

Use **Gradle 5.3** or later to compile the backend

Development mode compile

```sh
gradle build
```

Production mode compile

```sh
gradle build -Pprod
```

The generated war package is in the `build/libs` directory

### Front-end compile

Please refer to the respective `README.md` file in the `web` directory

## Use

### Initialize the database

In production mode, you must create a database before using Mysql.
After creating the database, you can initialize it by importing `deploy\init.sql`
or execute the installer `deploy\install.sh`

**Note**: Refer to `deploy/README.md`(Chinese Only) for instructions on `install.sh`

## Run

Execute in the deployed directory

```sh
 ./start.sh
```

After the server starts, visit the web page

* Mobile web page: http://ip:port/app-template/index.html
* Admin web page: http://ip:port/app-template/admin/index.html
* swagger ui: http://ip:port/app-template/swagger-ui.html

## Other instructions

### Database

Because of the difference between H2 and Mysql, the initialization SQL
scripts of the two are different.
In the `resources/db/` directory is used by H2 database initialization
* schema-h2.sql database structure
* data-h2.sql database data

In `deploy/init.sql` is the Mysql database initialization use.

### Why use context-path

`server.servlet.context-path=app-template`, the context path of the application,
also known as the project path, is part of the URL address.

After adding this configuration, **/app-template** will be automatically added
in front of the url, for example:
the original interface is localhost:8080/api/user, after adding this
configuration, it will become localhost:8080/app-template/api/user.

This configuration plays a role in ngnix distribution, and its main function is
to distribute all requests containing **/app-template** to the specified server.

If you do not use nginx for forwarding, you can delete the configuration.
**Note**: At the same time of deletion, you need to delete the app-template of
url in the front-end code.
