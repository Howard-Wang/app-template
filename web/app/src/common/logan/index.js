import Vue from 'vue';
import Logan from 'logan-web';
import { ToastPlugin, LoadingPlugin } from 'vux';
import store from '@/store';
import i18n from '@/common/i18n';

Vue.use(ToastPlugin);
Vue.use(LoadingPlugin);

const host = window.location.host;
Logan.initConfig({
  reportUrl: `http://${host}/app-template/api/logs/web/upload`,
  logTryTimes: 3,
  dbName: 'logan_web_db',
  errorHandler: function(e) {},
});

const logType = {
  INFO: 1, // INFO
  WARN: 2, // WARN
  ERROR: 3, // ERROR
};

const logan = {
  log(content, isEncrypt = false) {
    console.log('take notes logan', content);
    if (isEncrypt) {
      Logan.logWithEncryption(content, logType.INFO);
    } else {
      Logan.log(content, logType.INFO);
    }
  },
  warn(content, isEncrypt = false) {
    console.log('take notes logan', content);
    if (isEncrypt) {
      Logan.logWithEncryption(content, logType.WARN);
    } else {
      Logan.log(content, logType.WARN);
    }
  },
  error(content, isEncrypt = false) {
    console.log('take notes logan', content);
    if (isEncrypt) {
      Logan.logWithEncryption(content, logType.ERROR);
    } else {
      Logan.log(content, logType.ERROR);
    }
  },
  async report(fromDate, toDate) {
    const MobileDetect = require('mobile-detect');
    const device_type = navigator.userAgent; // 获取userAgent信息
    const md = new MobileDetect(device_type); // 实例化mobile-detect
    let os = md.os(); // 获取系统
    let model = '';
    if (os === 'iOS') {
      os = md.os() + md.version('iPhone');
      model = md.mobile();
    } else if (os === 'AndroidOS') {
      // Android系统的处理
      os = md.os() + md.version('Android');
    }
    console.log(os + '-' + model); // 打印系统版本和手机型号
    const me = store.state.me;
    console.log('me value is', me);
    const customInfo = {
      userId: me.userId,
      username: me.username,
    };
    const deviceId = os + '-' + customInfo.username + '-' + model;
    const deviceModel = model;
    Vue.$vux.loading.show({
      text: i18n.t('common.uploading'),
    });
    try {
      const reportResult = await Logan.report({
        deviceId: deviceId,
        webSource: deviceModel,
        environment: window.navigator.userAgent,
        fromDayString: fromDate,
        toDayString: toDate,
        customInfo: JSON.stringify(customInfo),
      });

      let result = true;
      for (const key in reportResult) {
        if (reportResult[key].msg === 'Report fail') {
          result = false;
        }
      }
      if (result) {
        Vue.$vux.toast.show({
          text: i18n.t('setting.uploadSucceeded'),
          time: 2000,
        });
      } else {
        Vue.$vux.toast.show({
          type: 'warn',
          text: i18n.t('setting.uploadFailed'),
          time: 2000,
        });
      }
      console.log(reportResult); // for debug
    } catch (error) {
      Vue.$vux.toast.show({
        type: 'warn',
        text: i18n.t('setting.uploadFailed'),
        time: 2000,
      });
      console.error('logan report error', error);
    } finally {
      Vue.$vux.loading.hide();
    }
  },
};

export default logan;
