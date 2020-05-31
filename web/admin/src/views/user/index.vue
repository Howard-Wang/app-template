<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="search.name"
        :placeholder="$t('department.username')"
        clearable
        class="filter-item"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="getUser">{{
        $t('common.search')
      }}</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="showAddFormDialog">{{
        $t('common.newAdd')
      }}</el-button>
    </div>

    <el-table
      :data="users"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column
        type="index"
        :label="$t('common.num')"
        align="center"
        width="80"
      />
      <el-table-column
        property="userId"
        :label="$t('department.userId')"
        align="center"
        width="250"
      />
      <el-table-column
        property="username"
        :label="$t('department.username')"
        align="center"
        width="200"
      />
      <el-table-column
        :label="$t('department.role')"
        align="center"
        width="200"
      >
        <template slot-scope="scope">
          <div>
            {{ getAuthorityText(scope.row.userRole) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operate')" align="center">
        <template slot-scope="scope">
          <el-button
            v-if="isNotSuperman(scope.row)"
            icon="el-icon-reset"
            class="oprate"
            size="mini"
            plain
            type="primary"
            @click.native.prevent="resetUserPwd(scope.row)"
          >{{ $t('user.resetUserPwd') }}</el-button>
          <el-button
            v-if="isNotSuperman(scope.row)"
            icon="el-icon-delete"
            class="oprate"
            size="mini"
            plain
            type="danger"
            @click.native.prevent="removeUser(scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="search.page" :limit.sync="search.size" @pagination="pagination" />

    <el-dialog :title="userFormTitle" :visible.sync="userFormVisible">
      <el-form
        ref="userForm"
        :rules="rules"
        :model="form"
        label-position="left"
        label-width="90px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item
          :label="$t('user.username')"
          prop="username"
        >
          <el-input
            v-model="form.username"
            :placeholder="$t('user.enterUsername')"
          />
        </el-form-item>

        <el-form-item :label="$t('user.password')" prop="password">
          <el-input
            v-model="form.password"
            :placeholder="$t('user.enterPassword')"
            show-password
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="userFormVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="saveUser"
        >
          {{ confirmText }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import user from '@/api/user';
import Pagination from '@/components/Pagination';
export default {
  name: 'Employee',
  components: { Pagination },
  data() {
    return {
      search: {
        name: null,
        page: 1,
        size: 10,
      },
      users: [],
      total: 0,
      form: {
        username: null,
        password: null,
        userRole: 'ROLE_USER',
      },
      userFormVisible: false,
    };
  },
  computed: {
    userFormTitle() {
      return this.$t('user.createUser');
    },
    confirmText() {
      return this.$t('common.create');
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
    this.refreshUser();
  },
  methods: {
    isNotSuperman(row) {
      return row.userRole !== 'ROLE_SUPERMAN';
    },
    resetUserForm() {
      this.form = {
        username: null,
        password: null,
        userRole: 'ROLE_USER',
      };
    },
    /**
     * 路由到添加页面
     */
    showAddFormDialog() {
      this.resetUserForm();
      this.userFormVisible = true;
      this.$nextTick(() => {
        this.$refs['userForm'].clearValidate();
      });
    },
    saveUser() {
      this.$refs['userForm'].validate(async valid => {
        if (valid) {
          await user.saveUser(this.form);
          this.$message({
            message: this.$t('user.saveUserSuccess'),
            type: 'success',
          });
          this.userFormVisible = false;
          this.refreshUser();
        }
      });
    },
    /**
     * 重置用户密码
     */
    async resetUserPwd(row) {
      await user.resetPwd(row.id);
      this.$message({
        message: this.$t('user.resetPwdSuccessfully'),
        type: 'success',
      });
    },
    /**
     * 删除用户
     */
    removeUser(row) {
      this.$confirm(this.$t('user.removeUserTips'), this.$t('common.tips'), {
        type: 'warning',
      }).then(async() => {
        await user.removeUser(row.id);
        this.handleCurrentChange(this.search.page);
        this.$message({
          message: this.$t('user.removeSuccessfully'),
          type: 'success',
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: this.$t('user.cancelRemove'),
        });
      });
    },
    // 重新加载用户数据
    refreshUser() {
      this.search.page = 1;
      this.getUser();
    },
    async getUser() {
      const res = await user.getUser(this.search);
      this.users = res.data;
      this.total = Number(res.headers['x-total-count']);
    },
    pagination() {
      this.getUser();
    },
    handleSizeChange(val) {
      this.search.size = val;
      this.getUser();
    },
    handleCurrentChange(val) {
      this.search.page = val;
      this.getUser();
    },
    getAuthorityText(role) {
      switch (role) {
        case 'ROLE_USER':
          return this.$t('user.roleUser');
        case 'ROLE_SUPERMAN':
          return this.$t('user.roleSuperman');
        default:
          return this.$t('user.roleUnknown');
      }
    },
  },
};
</script>

<style scoped>
.filter {
  display: flex;
  margin-left: 15px;
  margin-top: 15px;
  color: #333;
  font-size: 14px;
}
.department {
  margin-left: 15px;
}
.el-input {
  width: 250px;
}
.button {
  margin-left: 15px;
}
.table {
  width: 100%;
  margin-left: 10px;
  margin-top: 20px;
}
.pagination {
  margin-top: 20px;
  width: 100%;
  text-align: center;
}
</style>
