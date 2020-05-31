import axios from 'axios';
import { MessageBox, Message } from 'element-ui';
import store from '@/store';
import i18n from '@/lang';
import router from '@/router';
import { getToken } from '@/utils/auth';
import { getLanguage } from '@/lang';

// 获取访问 url 中的端口号
const host = window.location.host;

// create an axios instance
const service = axios.create({
  // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  baseURL: `http://${host}/app-template`,
  withCredentials: true, // send cookies when cross-domain requests
  credentials: 'include',
  timeout: 5000, // request timeout
});

const languages = {
  zh: 'zh-cn',
  en: 'en',
};
// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = getToken();
    }
    config.headers['accept-language'] = languages[getLanguage()];
    return config;
  },
  error => {
    // do something with request error
    console.log(error); // for debug
    return Promise.reject(error);
  }
);

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

async function logout() {
  await store.dispatch('user/logout');
  router.push('/login');
}

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data;
    // console.log(res);

    // if the custom code is not 20000, it is judged as an error.
    if (res.code && res.code !== 0 && res.code !== '0') {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000,
      });

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        MessageBox.confirm(
          'You have been logged out, you can cancel to stay on this page, or log in again',
          'Confirm logout',
          {
            confirmButtonText: 'Re-Login',
            cancelButtonText: 'Cancel',
            type: 'warning',
          }
        ).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload();
          });
        });
      }
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return response;
    }
  },
  error => {
    console.log('error.request', error.request); // for debug
    if (error.request.status === 401) {
      logout();
    }

    let message = '';
    const status = getCommonStatus(error.response.status);
    if (error.response && error.response.data) {
      const data = error.response.data;
      message = (data.desc || data.message) ? (data.desc || data.message) : i18n.t(`error.${status}`);
    } else {
      message = i18n.t(`error.${status}`);
    }

    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);

export default service;
