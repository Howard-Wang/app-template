import Vue from 'vue';
import { ToastPlugin, LoadingPlugin, ConfirmPlugin, AlertPlugin, TransferDom } from 'vux';
import VConsole from 'vconsole';
import moment from 'moment';

import i18n from '@/common/i18n';
import logan from '@/common/logan';
import App from './App';
import Utils from './components/utils';
import store from './store';
import router from './router';

import http from './components/utils/http';
import serverUrl from './components/utils/serverUrl';

Vue.config.productionTip = false;
Vue.config.devtools = true;

Vue.prototype.$eventBus = new Vue();

Vue.prototype.$http = http;
Vue.prototype.$serverUrl = serverUrl;
Vue.prototype.$logan = logan;
Vue.prototype.$moment = moment;
Vue.prototype.$utils = Utils;

Vue.use(ToastPlugin);
Vue.use(LoadingPlugin);
Vue.use(ConfirmPlugin);
Vue.use(AlertPlugin);

Vue.directive('transfer-dom', TransferDom);

Vue.use(VConsole, { name: 'v-console' });

/* eslint-disable no-new */
const vue = new Vue({
  el: '#app',
  router,
  i18n,
  store,
  beforeMount() {
    window.$vueDelegate = this;
    window.$vuxDelegate = this.$vux;
  },
  render: h => h(App),
});

export default vue;
