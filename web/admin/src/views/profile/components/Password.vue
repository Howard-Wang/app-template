<template>
  <el-form ref="userForm" :rules="rules" :model="pwdForm">
    <el-form-item :label="$t('profile.newPwd')" prop="newPwd">
      <el-input v-model.trim="pwdForm.newPwd" show-password />
    </el-form-item>
    <el-form-item :label="$t('profile.confirmPwd')" prop="confirmPwd">
      <el-input v-model.trim="pwdForm.confirmPwd" show-password />
    </el-form-item>
    <!-- <el-form-item :label="$t('profile.oldPwd')" prop="oldPwd">
      <el-input v-model.trim="pwdForm.oldPwd" show-password />
    </el-form-item> -->
    <el-form-item>
      <el-button type="primary" @click="modifyUserPwd">{{ $t('common.modify') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import userApi from '@/api/user';

export default {
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          name: '',
          email: '',
        };
      },
    },
  },
  data() {
    return {
      pwdForm: {
        newPwd: null,
        confirmPwd: null,
      },
    };
  },
  computed: {
    rules() {
      const validatePassword = (rule, value, callback) => {
        if (!value) {
          callback(new Error(this.$t('profile.confirmPwdNotEmpty')));
        } else if (value.length < 6) {
          callback(new Error(this.$t('login.passwordDigits')));
        } else {
          callback();
        }
      };
      const validateConfirmPwd = (rule, value, callback) => {
        if (!value) {
          callback(new Error(this.$t('profile.confirmPwdNotEmpty')));
        } else if (value.length < 6) {
          callback(new Error(this.$t('login.passwordDigits')));
        } else if (this.pwdForm.newPwd !== value) {
          callback(new Error(this.$t('profile.confirmPwdDiffferentNewPwd')));
        } else {
          callback();
        }
      };
      return {
        newPwd: [{ required: true, trigger: 'blur', validator: validatePassword }],
        confirmPwd: [{ required: true, trigger: 'blur', validator: validateConfirmPwd }],
      };
    },
  },
  methods: {
    modifyUserPwd() {
      this.$refs['userForm'].validate(async valid => {
        if (valid) {
          const param = {
            password: this.pwdForm.newPwd,
          };
          await userApi.modifyUser(this.user.id, param);
          this.$message({
            message: this.$t('profile.modifyPwdSuccessfully'),
            type: 'success',
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.input-width {
  width: 250px;
}
</style>
