<template>
  <div class="login-box">
    <div class="home-top">
      <x-icon type="ios-close-outline" size="22" class="arrow" @click="close" />
    </div>

    <div
      v-if="type == 'loading'"
      class="no-right-loading"
      style="color:#000;text-align:center;"
    >
      Loading ...
    </div>

    <div v-if="type == 'login'" class="login">
      <group>
        <x-input
          :placeholder="$t('login.userName')"
          v-model="loginName"
          :show-clear="false"
          type="text"
          style="margin-bottom: 10px"
        />
        <div class="pass-line">
          <x-input
            :placeholder="$t('login.password')"
            v-model="loginPass"
            :type="inputType"
            :show-clear="false"
          />
          <img
            v-show="icoFlag"
            src="@/assets/setting/eye-close-gray.png"
            @click="
              icoFlag = false;
              inputType = 'text';
            "
          />
          <img
            v-show="!icoFlag"
            src="@/assets/setting/eye-open-gray.png"
            @click="
              icoFlag = true;
              inputType = 'password';
            "
          />
        </div>
      </group>

      <br />

      <x-button :gradients="['#58affd', '#58affd']" @click.native="loginFun">
        {{ $t('login.signIn') }}
      </x-button>

      <p class="title-sub">{{ $t('login.defaultAccount') }}：admin / 123456</p>
    </div>
  </div>
</template>

<script>
import { Group, XInput, XButton, Toast, Spinner } from 'vux';

export default {
  name: 'Login',
  components: {
    Group,
    XInput,
    XButton,
    Toast,
    Spinner,
  },
  data() {
    return {
      loginName: '',
      loginPass: '',
      type: 'login',
      icoFlag: true,
      inputType: 'password',
      time: null,
      sec: 5,
      role: null, // 指定角色
      showDefault: false, // 控制默认账号密码的显示
    };
  },
  mounted() {
    this.$logan.log('get user info');
  },
  methods: {
    // 关闭窗口
    close() {
      try {
        this.$vux.alert.show({
          title: this.$t('common.tips'),
          content: this.$t('common.functionNotImplemented'),
          buttonText: this.$t('common.ok'),
        });
        // eslint-disable-next-line no-empty
      } catch (error) {
        this.$logan.error(error);
      }
    },
    // 获取用户信息
    userInfo() {
      this.$http.fetchGet(this.$serverUrl.me).then(res => {
        if (res.status === 200 && res.data.userRole !== '') {
          this.$logan.log('用户登录成功');
          this.$store.commit('me', res.data);
          this.$router.replace('/home');
        } else {
          this.type = 'login';
          this.showDefault = true;
        }
      });
    },
    tempLogin() {
      this.role = 'USER';
      // 去掉用户名和密码否则将以默认用户的权限登陆
      this.loginName = '';
      this.loginPass = '';
      this.login();
    },
    // 登录
    loginFun() {
      if (this.loginName === '' || this.loginPass === '') {
        this.$vux.toast.show({
          type: 'text',
          position: 'top',
          text: this.$t('login.inputNoEmpty'),
        });
        return;
      }

      this.login();
    },
    login() {
      const params = {
        username: this.loginName,
        password: this.loginPass,
      };

      this.$http.fetchPost(this.$serverUrl.login, params).then(res => {
        this.$logan.log(JSON.stringify(res));
        const token = res.headers['authorization'];
        localStorage.authorization = token;
        this.$logan.log('login success');
        this.userInfo();
      });
    },
    // 无权使用，5 秒自动关闭
    autoClose() {
      let i = 0;
      this.time = setInterval(() => {
        i += 1;
        if (i < 5) {
          this.sec = 5 - i;
        } else {
          clearInterval(this.time);
          this.close();
        }
      }, 1000);
    },
    GetUrlParam(paraName) {
      const url = document.location.toString();
      const arrObj = url.split('?');

      if (arrObj.length > 1) {
        const arrPara = arrObj[1].split('&');
        let arr;

        // eslint-disable-next-line no-plusplus
        for (let i = 0; i < arrPara.length; i++) {
          arr = arrPara[i].split('=');

          if (arr != null && arr[0] === paraName) {
            return arr[1];
          }
        }
        return '';
      }
      return '';
    },
  },
};
</script>

<style lang="less" scoped>
.login-box {
  align-content: center;
  align-items: center;
}
.home-top {
  position: absolute;
  top: 0;
  width: 100%;
  height: 2rem;
  line-height: 2rem;
}
.home-top span {
  float: right;
  margin-right: 0.7rem;
  color: #007aff;
  font-size: 0.9rem;
}
.home-top .arrow {
  fill: #007aff;
  position: absolute;
  top: 15px;
  left: 15px;
}
.logo {
  display: block;
  width: 22%;
  height: 22%;
  margin: 4rem auto 2rem auto;
}
.login {
  /* width: 70%; */
  margin: 40px auto 0 auto;
  padding: 1rem 3rem;
  border-radius: 5px;
}
.title {
  color: #666;
  font-size: 16px;
}
.title-sub {
  color: #333;
  font-size: 80%;
  padding-top: 10px;
}
.tip-line {
  height: 2rem;
  line-height: 2rem;
  margin: 1.5rem 0 0.6rem 0;
  color: #f00;
  text-align: center;
  font-size: 16px;
}
.sec-line {
  margin-bottom: 2rem;
  color: #999;
  text-align: center;
  font-size: 16px;
}
.no-right {
  position: fixed;
  top: 35%;
  left: 50%;
  margin-left: -35%;
  box-sizing: border-box;
}
.no-right-loading {
  box-sizing: border-box;
}
.pass-line {
  position: relative;
}
.pass-line img {
  position: absolute;
  top: 3px;
  right: 20px;
}
.user-login {
  width: 100%;
  padding-top: 20px;
  color: #58affd;
  text-align: center;
  font-size: 115%;
}
/deep/ .weui-cells:before {
  content: '';
  border-top: 1px solid #fff;
}
/deep/ .weui-input {
  background: #fdfdfd;
}
/deep/ .weui-cell {
  border: 1px solid lightgray;
}
</style>
