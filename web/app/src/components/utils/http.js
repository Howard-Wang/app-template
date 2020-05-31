import axios from 'axios';
import Vue from 'vue';
import { ToastPlugin, LoadingPlugin, AlertPlugin } from 'vux';
import logan from '@/common/logan';
import i18n from '@/common/i18n';

Vue.use(ToastPlugin);
Vue.use(LoadingPlugin);
Vue.use(AlertPlugin);

axios.defaults.withCredentials = true;
axios.defaults.timeout = 5000; // 响应时间
// axios.defaults.headers.post["Content-Type"] =
//   "application/x-www-form-urlencoded;charset=UTF-8"; // 配置请求头

// 发布请求 axios baseURL 地址配置
const host = window.location.host;
axios.defaults.baseURL = `http://${host}/app-template`;

function getCommonStatus(status) {
  if (status === 404 || status === 400) {
    return `${status}`;
  }

  if (status >= 400 && status < 500) {
    return '40X';
  }

  if (status >= 500) {
    return '50X';
  }

  return 'unknown';
}

function errorHandler(error) {
  if (error.response.status === 401) {
    logan.error(error.response.config.url);
    if (error.response.config.url.includes('/api/login')) {
      handleErrorMsg(error);
      return;
    }

    Vue.$vux.alert.show({
      title: i18n.t('common.tips'),
      content: i18n.t('common.noSession'),
    });
    // TODO 需要关闭应用
    return;
  }

  handleErrorMsg(error);
}

function handleErrorMsg(error) {
  let message = '';
  const status = getCommonStatus(error.response.status);
  if (error.response && error.response.data) {
    const data = error.response.data;
    message = (data.desc || data.message) ? (data.desc || data.message) : i18n.t(`error.${status}`);
  } else {
    message = i18n.t(`error.${status}`);
  }

  Vue.$vux.toast.show({
    text: message,
    type: 'warn',
    time: 4000,
    width: '10em',
    position: 'top',
  });
}

const languages = {
  zh: 'zh-cn',
  en: 'en',
};
// request interceptor
axios.interceptors.request.use(
  config => {
    // do something before request is sent
    if (localStorage.authorization) {
      config.headers['Authorization'] = localStorage.authorization;
    }
    config.headers['accept-language'] = languages[localStorage.language];
    return config;
  },
  error => {
    // do something with request error
    logan.error(JSON.stringify(error));
    return Promise.reject(error);
  }
);

// Add a response interceptor
axios.interceptors.response.use(
  response =>
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    response
  ,
  (error) => {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    logan.error(JSON.stringify(error));
    errorHandler(error);
    return Promise.reject(error);
  },
);

export function fetchPost(url, params, config) {
  return new Promise((resolve, reject) => {
    axios.post(url, params, config).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        reject(err);
      },
    );
  });
}

export function fetchGet(url, param) {
  return new Promise((resolve, reject) => {
    axios.get(url, { params: param }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        reject(err);
      },
    );
  });
}

export function fetchDelete(url, param) {
  return new Promise((resolve, reject) => {
    axios.delete(url, { params: param }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        reject(err);
      },
    );
  });
}

export function fetchPatch(url, param) {
  return new Promise((resolve, reject) => {
    axios.patch(url, param).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        reject(err);
      },
    );
  });
}

export function download(url, callback) {
  return new Promise((resolve, reject) => {
    axios.get(url, {
      responseType: 'blob',
      onDownloadProgress: (event) => {
        callback(event);
      },
    }).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        reject(err);
      },
    );
  });
}

export default {
  fetchPost,
  fetchGet,
  fetchDelete,
  fetchPatch,
  download,
};
