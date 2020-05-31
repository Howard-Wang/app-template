<template>
  <div class="home">
    <div class="header">
      <x-header :left-options="{ showBack: false }">
        <header-title :title="$t('home.home')" />
        <a slot="left" @click="onClose">{{ $t('common.close') }}</a>
        <a slot="right" @click="onSetting">{{ $t('common.setting') }}</a>
      </x-header>
      <div v-if="$i18n.locale === 'zh'" class="introduce">
        <p class="title">欢迎使用应用模板</p>
        <P class="small-title">功能支持如下：</P>
        <ul>
          <li>使用 VUX 组件库</li>
          <li>支持 Logan 前端日志上传</li>
          <li>支持权限验证功能</li>
          <li>支持语言国际化，默认采用中文作为语言，可在设置切换英文</li>
        </ul>
      </div>
      <div v-else class="introduce-en">Welcome!</div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import { InlineLoading, XButton, XHeader, Group, Cell } from 'vux';
import HeaderTitle from '@/components/common/HeaderTitle';

export default {
  name: 'Home',
  components: {
    InlineLoading,
    XButton,
    XHeader,
    HeaderTitle,
    Group,
    Cell,
  },
  computed: {
    ...mapState(['me', 'home']),
  },
  created() {
    // 第三方登录获取 token, 保存到 localStorage 中
    this.$logan.log('welcome to home page');

    const token = this.$route.query.token;
    if (token) {
      localStorage.authorization = token;
    }
    this.userInfo();
  },
  methods: {
    ...mapActions(['setDoodHost']),
    // 获取用户信息
    userInfo() {
      this.$http.fetchGet(this.$serverUrl.me).then(data => {
        if (data.status === 200 && data.data.userRole !== '') {
          this.$logan.log('用户登录成功');
          this.$store.commit('me', data.data);
        }
      });
    },
    onSetting() {
      this.$router.push({ path: '/setting' });
    },
    onClose() {
      this.$vux.alert.show({
        title: this.$t('common.tips'),
        content: this.$t('common.functionNotImplemented'),
        buttonText: this.$t('common.ok'),
      });
    },
  },
};
</script>

<style lang="less" scoped>
.home {
  background-color: #f6f6f6;
  height: 100%;
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
  .content {
    height: calc(100% - 45px);
    .demo-button {
      width: 80%;
      top: 40%;
    }
  }
  .introduce {
    padding: 15px;
    margin-top: 47px;
    .title {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 10px;
    }
    .small-title {
      font-size: 16px;
      margin-bottom: 10px;
    }
    li {
      line-height: 30px;
      list-style: disc !important;
      margin-left: 15px;
    }
  }
  .introduce-en{
     padding: 30px;
     margin-top: 47px;
     text-align: center;
     font-size:20px;
     font-weight: bold;
  }
}
</style>
