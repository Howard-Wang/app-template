<template>
  <div class="employeeForm">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item v-if="isNotAdmin" :label="$t('user.username')" prop="username">
        <el-input v-model="form.username" :placeholder="$t('user.enterUsername')" />
      </el-form-item>
      <el-form-item :label="$t('user.password')" prop="password">
        <el-input
          v-model="form.password"
          :placeholder="$t('user.enterPassword')"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitSysUser">{{ submitText }}</el-button>
        <el-button @click="routeListView">{{ $t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { saveUser, getUserById, modifyUser } from '@/api/user';
export default {
  name: 'UserForm',
  data() {
    return {
      id: null,
      form: {
        username: null,
        password: null,
        userRole: 'ROLE_USER',
      },
    };
  },
  computed: {
    submitText() {
      const createText = this.$t('common.create');
      const modifyText = this.$t('common.modify');
      return this.id ? modifyText : createText;
    },
    isNotAdmin() {
      return !(this.id && this.form.username === 'admin');
    },
    rules() {
      const validatePassword = (rule, value, callback) => {
        if (!value) {
          callback(new Error(this.$t('user.enterPassword')));
        } else if (value.length < 6) {
          callback(new Error(this.$t('login.passwordDigits')));
        } else {
          callback();
        }
      };
      return {
        username: [{ required: true, message: this.$t('user.enterUsername'), trigger: 'blur' }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
      };
    },
  },
  created() {
    if (this.$route.query.id) {
      this.id = this.$route.query.id;
      this.getSysUser();
    }
  },
  methods: {
    async getSysUser() {
      const res = await getUserById(this.id);
      this.form = {
        username: res.data.username,
        userRole: res.data.userRole,
        password: null,
      };
    },
    routeListView() {
      this.$router.push('/contacts/user');
    },
    submitSysUser() {
      if (this.id) {
        this.modifySysUser();
      } else {
        this.saveSysUser();
      }
    },
    saveSysUser() {
      this.$refs['form'].validate(async valid => {
        if (valid) {
          await saveUser(this.form);
          this.$message({
            message: this.$t('user.saveUserSuccess'),
            type: 'success',
          });
          this.routeListView();
        }
      });
    },
    modifySysUser() {
      this.$refs['form'].validate(async valid => {
        if (valid) {
          await modifyUser(this.id, this.form);
          this.$message({
            message: this.$t('user.saveUserSuccess'),
            type: 'success',
          });
          this.routeListView();
        }
      });
    },
  },
};
</script>

<style scoped>
.employeeForm {
  width: 350px;
  padding-top: 20px;
}
</style>
