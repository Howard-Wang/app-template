<template>
  <div class="setting">
    <div class="header">
      <x-header :left-options="{ backText: '' }">
        <header-title :title="$t('common.setting')" />
      </x-header>
    </div>

    <div class="list-box">
      <group gutter="10px">
        <cell
          :title="$t('setting.language')"
          :value="language"
          is-link
          @click.native="languagesVisible = true"
        />
        <cell :title="$t('setting.uploadLog')">
          <div slot="child">
            <x-button mini type="primary" @click.native="uploadLog">{{
              $t('common.upload')
            }}</x-button>
          </div>
        </cell>
        <authorized :authority="['ROLE_SUPERMAN']">
          <cell :title="$t('setting.onlySupermanVisible')" />
        </authorized>
        <cell
          :title="$t('setting.about')"
          is-link
          @click.native="onAbout"
        />
      </group>
    </div>

    <actionsheet
      v-model="languagesVisible"
      :menus="languages"
      theme="android"
      @on-click-menu="changeLanguage"
    />
  </div>
</template>

<script>
import i18n from '@/common/i18n';
import { Cell, Group, XSwitch, XButton, Actionsheet, XHeader } from 'vux';
import { mapGetters } from 'vuex';
import HeaderTitle from '@/components/common/HeaderTitle';
import Authorized from '@/components/common/Authorized';

export default {
  name: 'Setting',
  components: {
    Cell,
    Group,
    XSwitch,
    XHeader,
    HeaderTitle,
    XButton,
    Authorized,
    Actionsheet,
  },
  data() {
    return {
      languagesVisible: false,
    };
  },
  computed: {
    ...mapGetters(['me']),
    languages() {
      return {
        zh: '中文',
        en: 'English',
      };
    },
    language() {
      return i18n.locale === 'en' ? this.$t('setting.english') : this.$t('setting.chinese');
    },
  },
  methods: {
    onAbout() {
      this.$router.push({ path: '/about' });
    },
    changeLanguage(key, item) {
      localStorage.language = key;
      i18n.locale = key;
    },
    uploadLog() {
      this.$vux.confirm.show({
        title: this.$t('common.confirm'),
        content: this.$t('setting.confirmUploadLog'),
        confirmText: this.$t('common.ok'),
        cancelText: this.$t('common.cancel'),
        onConfirm: () => {
          const fromDay = this.$moment()
            .subtract(6, 'days')
            .format('YYYY-MM-DD');
          const toDay = this.$moment().format('YYYY-MM-DD');
          this.$logan.report(fromDay, toDay);
        },
      });
    },
  },
};
</script>

<style lang="less" scoped>
.setting {
  height: 100%;
  overflow: hidden;
  .header {
    position: relative;
    height: 45px;
  }
  /deep/ .vux-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
  }
  .list-box {
    height: calc(100% - 45px);
    background: #f7f7f7;
    box-sizing: border-box;
  }
}
</style>
