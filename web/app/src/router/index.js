import Vue from 'vue';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css'; // 引入样式
import Router from 'vue-router';

Vue.use(Router);

const router = new Router({
  // TODO 如果要使用 history 模式需要后端一起支持
  // 参考: https://router.vuejs.org/zh/guide/essentials/history-mode.html
  // mode: 'history',
  routes: [
    // 首页
    {
      path: '*',
      redirect: '/home',
    },
    {
      path: '/',
      name: 'Login',
      component: resolve => require(['@/components/login'], resolve),
    },
    {
      path: '/home',
      name: 'Home',
      component: resolve => require(['@/components/home'], resolve),
      meta: {
        keepAlive: false, // 演示如何使用 keepAlive
      },
    },
    // 设置
    {
      path: '/setting',
      name: 'Setting',
      component: resolve => require(['@/components/setting/setting'], resolve),
    },
    // 关于
    {
      path: '/about',
      name: 'About',
      component: resolve =>
        require(['@/components/setting/about'], resolve),
    },
  ],
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
