import Vue from 'vue';
import moment from 'moment';

import 'normalize.css/normalize.css'; // A modern alternative to CSS resets

import 'element-ui/lib/theme-chalk/index.css';
import links from '@/utils/links';
import permission from '@/directive/permission/index.js';

import '@/styles/index.scss'; // global css

import App from './App';
import store from './store';
import router from './router';
import i18n from './lang'; // internationalization

import '@/icons'; // icon
import '@/permission'; // permission control

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock');
//   mockXHR();
// }

Vue.prototype.$moment = moment;
Vue.prototype.$links = links;

Vue.use(permission);

Vue.config.productionTip = false;

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App),
});
