<template>
  <div class="app-container">
    <div>
      <el-row :gutter="20">

        <el-col :span="6" :xs="24">
          <user-card :user="user" />
        </el-col>

        <el-col :span="18" :xs="24">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane :label="$t('login.modifyUserPwd')" name="password">
                <password :user="user" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import UserCard from './components/UserCard';
import Password from './components/Password';

export default {
  name: 'Profile',
  components: { UserCard, Password },
  data() {
    return {
      user: {},
      activeTab: 'infomataion',
    };
  },
  computed: {
    ...mapGetters([
      'id',
      'name',
      'avatar',
      'roles',
    ]),
  },
  created() {
    this.getUser();
    this.activeTab = this.$route.params.activeTab;
  },
  methods: {
    getUser() {
      this.user = {
        id: this.id,
        name: this.name,
        role: this.roles.join(' | '),
        avatar: this.avatar,
      };
    },
  },
};
</script>
