import Vue from 'vue';
import Vuex from 'vuex';
import getters from './getters';
import app from './modules/app';
import settings from './modules/settings';
import user from './modules/user';
import permission from './modules/permission';
// import createLogger from 'vuex/dist/logger';

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    app,
    user,
    settings,
    permission,
  },
  getters,
// plugins: [createLogger()],
});

export default store;
