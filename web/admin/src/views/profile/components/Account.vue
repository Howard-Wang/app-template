<template>
  <el-form ref="userForm" :model="userForm" :rules="rules">
    <el-form-item :label="$t('user.username')" prop="username">
      <el-input v-model.trim="userForm.username" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="modifyUser">{{ $t('common.modify') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapActions } from 'vuex';
import userApi from '@/api/user';

export default {
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          id: '',
          name: '',
        };
      },
    },
  },
  data() {
    return {
      userForm: {
        username: this.user.name,
      },
    };
  },
  computed: {
    rules() {
      return {
        username: [{ required: true, message: this.$t('user.enterUsername'), trigger: 'blur' }],
      };
    },
  },
  methods: {
    ...mapActions('user', [
      'getInfo',
    ]),
    modifyUser() {
      this.$refs['userForm'].validate(async valid => {
        if (valid) {
          await userApi.modifyUser(this.user.id, this.userForm);
          this.$message({
            message: this.$t('user.saveUserSuccess'),
            type: 'success',
          });
        }
      });
    },
  },
};
</script>
