<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="search.customReportInfo"
        :placeholder="$t('serviceLog.username')"
        class="filter-item"
        clearable
      />
      <el-date-picker
        v-model="datetimeRange"
        type="datetimerange"
        :range-separator="$t('common.to')"
        :start-placeholder="$t('common.startTime')"
        :end-placeholder="$t('common.endTime')"
        class="filter-item"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="refreshClientLog">{{
        $t('common.search')
      }}</el-button>
      <el-button class="filter-item" type="danger" @click="removeClientLog">{{
        $t('common.delete')
      }}</el-button>
    </div>

    <el-table
      :data="logs"
      border
      fit
      highlight-current-row
      style="width: 100%;"
      @selection-change="changeSelectedLogs"
    >
      <el-table-column
        type="selection"
        align="center"
        width="55"
      />
      <el-table-column
        type="index"
        :label="$t('common.num')"
        align="center"
        width="80"
      />
      <el-table-column
        property="deviceId"
        :label="$t('serviceLog.deviceId')"
        align="center"
        width="200"
      />
      <el-table-column
        property="username"
        :label="$t('serviceLog.username')"
        align="center"
        width="200"
      />
      <el-table-column
        property="logDateStr"
        :label="$t('serviceLog.logDate')"
        align="center"
        width="100"
      />
      <el-table-column
        property="addTimeStr"
        :label="$t('serviceLog.addTime')"
        align="center"
        width="180"
      />
      <el-table-column
        property="webSource"
        :label="$t('serviceLog.webSource')"
        align="center"
        width="100"
      />
      <el-table-column
        property="environment"
        :label="$t('serviceLog.environment')"
        align="center"
      />
      <el-table-column
        :label="$t('common.operate')"
        align="center"
        width="100"
      >
        <template slot-scope="scope">
          <el-button
            icon="el-icon-download"
            class="oprate"
            size="mini"
            plain
            type="primary"
            @click.native.prevent="downloadLog(scope.row)"
          >{{ $t('common.download') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="search.page" :limit.sync="search.size" @pagination="pagination" />

  </div>
</template>

<script>
import { saveAs } from 'file-saver';
import log from '@/api/console';
import Pagination from '@/components/Pagination';

export default {
  name: 'Employee',
  components: { Pagination },
  data() {
    return {
      search: {
        beginTime: null,
        endTime: null,
        customReportInfo: null,
        page: 1,
        size: 10,
      },
      logs: [],
      total: 0,
      datetimeRange: [],
      selectedLogs: [],
    };
  },
  created() {
    this.refreshClientLog();
  },
  methods: {
    /**
     * 路由到添加页面
     */
    async downloadLog(row) {
      const params = { tasks: row.taskId };
      const res = await log.downloadClientLog(params);
      let filename = 'log';
      const content = res.headers['content-disposition'];
      const contentArr = content.split('=');
      if (contentArr.length > 1) {
        filename = contentArr[1].substring(1, contentArr[1].length - 1);
      }
      saveAs(new Blob([res.data]), filename);
      this.$message({
        message: this.$t('serviceLog.downloadSuccess'),
        type: 'success',
      });
    },
    // 重新加载用户数据
    refreshClientLog() {
      this.search.page = 1;
      this.getClientLog();
    },
    async getClientLog() {
      if (this.datetimeRange && this.datetimeRange.length === 2) {
        this.search.beginTime = this.datetimeRange[0].getTime();
        this.search.endTime = this.datetimeRange[1].getTime();
      } else {
        this.search.beginTime = null;
        this.search.endTime = null;
      }
      const res = await log.getClientLog(this.search);
      this.logs = [];
      res.data.forEach(element => {
        const item = Object.assign({}, element);
        if (item.customReportInfo) {
          const customInfo = JSON.parse(item.customReportInfo);
          item.userId = customInfo.userId;
          item.username = customInfo.username;
        }
        item.addTimeStr = this.$moment(item.addTime).format('YYYY-MM-DD HH:mm:ss');
        item.logDateStr = this.$moment(item.logDate).format('YYYY-MM-DD');
        this.logs.push(item);
      });
      this.total = Number(res.headers['x-total-count']);
    },
    pagination() {
      this.getClientLog();
    },
    async removeClientLog() {
      if (this.selectedLogs.length <= 0) {
        this.$message(this.$t('serviceLog.pleaseSelectedLog'));
        return;
      }
      this.$confirm(this.$t('serviceLog.removeClientLogTips'), this.$t('common.tips'), {
        type: 'warning',
      }).then(async() => {
        const taskIds = [];
        this.selectedLogs.forEach(element => {
          taskIds.push(element.taskId);
        });      const param = {
          tasks: taskIds.toString(),
        };
        await log.removeClientLog(param);
        this.$message({
          message: this.$t('serviceLog.removeClientLogSuccessfully'),
          type: 'success',
        });
        this.getClientLog();
      }).catch(() => {
        this.$message({
          type: 'info',
          message: this.$t('common.cancelRemove'),
        });
      });
    },
    changeSelectedLogs(val) {
      this.selectedLogs = val;
    },
    handleSizeChange(val) {
      this.search.size = val;
      this.getClientLog();
    },
    handleCurrentChange(val) {
      this.search.page = val;
      this.getClientLog();
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
  padding: 0 20px 0;
  width: 100%;
  text-align: right;
}
</style>
