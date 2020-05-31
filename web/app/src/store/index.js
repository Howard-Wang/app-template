import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {},
  state: {
    // 当前登录用户信息
    me: {
      id: null,
      userId: null,
      userToken: null,
      enterpriseId: null,
      username: null,
      userRole: null, // 角色
    },
    fileStatus: {}, // 文件状态
    isLoading: false, // 是否正在上传文件
    loadingText: '', // 文件上传下载进度文本
    doodHost: null,
    isSaveMembers: false,
  },
  getters: {
    // 获取当前用户的授权
    userCurrentAuthority: state => state.me.userRole,
    me: state => state.me,
  },
  mutations: {
    // 用户信息
    me(state, pass) {
      state.me = pass;
    },
    updateSaveMembers(state, status) {
      state.isSaveMembers = status;
    },
    setDoodHost(state, doodHost) {
      state.doodHost = doodHost;
    },
    setIsLoading(state, isLoading) {
      state.isLoading = isLoading;
    },
    setLoadingText(state, loadingText) {
      state.loadingText = loadingText;
    },
    updateFileStatus(state, data) {
      Vue.set(state.fileStatus, data.key, data.value);
    },
  },
  actions: {
    setDoodHost({ commit }, doodHost) {
      commit('setDoodHost', doodHost);
    },
    setIsLoading({ commit }, isLoading) {
      commit('setIsLoading', isLoading);
    },
    setLoadingText({ commit }, loadingText) {
      commit('setLoadingText', loadingText);
    },
  },
});
