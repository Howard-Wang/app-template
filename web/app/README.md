# 介绍

[English](./README.en.md)

应用模板的前端程序，使用 **vue** 开发

## 文件说明

```
.
├── App.vue
├── assets
│   └── setting           // 设置页面使用的图片
├── common
│   ├── css
│   │   └── init.css      // 基础公共样式
│   │   └── theme.css     // 重载的　vux 默认样式
│   ├── i18n
│   │   └── en.js         // 国际化英文资源文件
│   │   └── index.js      // 国际化配置文件
│   │   └── zh.js         // 国际化中文资源文件
│   ├── logan
│   │   └── index.js      // logan日志上传
├── components
│   ├── common
│   │   ├── Authorized.vue       // 控制界面元素显示不显示
│   │   ├── HeaderTitle.vue      // 公用的头部组件
│   ├── home
│   │   └── index.vue            // 首页
│   ├── login
│   │   └── index.vue            // 登录页
│   ├── setting
│   │   ├── About.vue            // 关于应用
│   │   ├── Setting.vue          // 设置
│   ├── utils
│   │   ├── auth.js              // 认证相关的函数
│   │   ├── http.js              // 封装了 axios 进行网络操作
│   │   ├── index.js             // 工具
│   │   ├── serverUrl.js         // 服务端接口的　URL
├── main.js
├── router
│   └── index.js                 // 路由
└── store
    └── index.js                 // vuex 数据管理
```

## 编译和运行

因为在国内 npm 的源会被墙掉，所以建议使用淘宝的源，或者使用 **cnpm**, 配置参考[这里](https://npm.taobao.org/)

``` bash
# install dependencies
npm install

# 开发模式，使用 mock 数据
npm run dev

# 开发模式，不使用 mock 数据
npm run dev:no-mock

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# 对代码规范进行扫描
npm run lint
```

## 测试

使用 mock 数据来测试

```sh
cnpm run dev
```

不使用 mock 数据来测试

```sh
cnpm run dev:no-mock
```
