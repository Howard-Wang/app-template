import Vue from 'vue';
import VueI18n from 'vue-i18n';
import zh from '@/common/i18n/zh';
import en from '@/common/i18n/en';

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'zh', // 语言标识
  messages: {
    zh,
    en,
  },
});

localStorage.language = 'zh';

export default i18n;
