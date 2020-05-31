<template>
  <div :class="classObj" class="app-wrapper">
    <div
      v-if="device === 'mobile' && sidebar.opened"
      class="drawer-bg"
      @click="handleClickOutside"
    />
    <sidebar class="sidebar-container" />
    <div class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @getVersion="getVersion" />
      </div>
      <app-main />
    </div>

    <el-dialog :visible.sync="centerDialogVisible" width="30%" center>
      <img
        class="app-logo"
        src="https://wpimg.wallstcn.com/69a1c46c-eb1c-4b46-8bd4-e9e686ef5251.png"
      >
      <p class="app-name">{{ $t('common.title') }}</p>
      <p class="version">{{ $t('common.version') }}：{{ version }}</p>
    </el-dialog>
  </div>
</template>

<script>
import { Navbar, Sidebar, AppMain } from './components';
import ResizeMixin from './mixin/ResizeHandler';

export default {
  name: 'Layout',
  components: {
    Navbar,
    Sidebar,
    AppMain,
  },
  mixins: [ResizeMixin],
  data() {
    return {
      centerDialogVisible: false, // 判断是否打开关于的弹窗
      version: '', // 版本号
    };
  },
  computed: {
    sidebar() {
      return this.$store.state.app.sidebar;
    },
    device() {
      return this.$store.state.app.device;
    },
    fixedHeader() {
      return true;
      // return this.$store.state.settings.fixedHeader;
    },
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile',
      };
    },
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false });
    },
    // 获取版本号
    getVersion(data) {
      this.centerDialogVisible = data.showDialog;
      this.version = data.version;
    },
  },
};
</script>

<style lang="scss" scoped>
@import '~@/styles/mixin.scss';
@import '~@/styles/variables.scss';

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;
  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}
.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$sideBarWidth});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.mobile .fixed-header {
  width: 100%;
}
.footer {
  width: calc(100% - #{$sideBarWidth});
  margin-left: 8%;
}
.app-logo {
  display: block;
  width: 100px;
  margin: 0 auto;
  margin-bottom: 30px;
}
.app-name {
  text-align: center;
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 20px;
}
.version {
  text-align: center;
  font-size: 14px;
}
</style>
