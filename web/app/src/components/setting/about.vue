<template>
  <div class="about">
    <div class="header">
      <x-header :left-options="{ backText: '' }">
        <header-title :title="$t('setting.about')" />
      </x-header>
    </div>

    <div class="list-box">
      <div class="about-yingyong">
        <img src="@/assets/setting/favicon.png" >
        <p class="muban">{{ $t('setting.appTemplate') }}</p>
        <p>{{ version }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { Cell, Group, XHeader } from 'vux';
import HeaderTitle from '@/components/common/HeaderTitle';

export default {
  name: 'About',
  components: {
    Cell,
    Group,
    XHeader,
    HeaderTitle,
  },
  data() {
    return {
      version: 'Version',
      clickTimes: 0,
    };
  },
  created() {
    this.clickTimes = 0;
    this.getVersion();
  },
  methods: {
    getVersion() {
      this.$http.fetchGet(this.$serverUrl.versions).then((data) => {
        if (data.data != null) {
          this.version = `Version ${data.data.version}`;
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.about {
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
    padding: 47px 0 10px 0;
    .about-yingyong {
      text-align: center;
    }
    .about-yingyong img {
      width: 80px;
      width: 80px;
      padding: 15px 0 0px;
    }
    .muban {
      font-size: 21px;
      font-weight: 700;
      font-family: '黑体';
    }
  }
}
</style>
