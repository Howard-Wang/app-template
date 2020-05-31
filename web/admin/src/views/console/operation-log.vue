<template>
  <div class="runtime-log">
    <el-row>
      <el-col :span="5">
        <div class="header-title">
          {{ $t('serviceLog.Journal') }}
        </div>
      </el-col>
      <el-col :span="5">
        <div class="header-font">
          <span>{{ $t('serviceLog.fontSize') }}</span>
          <el-link type="primary" @click="cutFontSize('big')">{{
            $t('serviceLog.fontSizeBig')
          }}</el-link>
          <el-link type="primary" @click="cutFontSize('middle')">{{
            $t('serviceLog.fontSizeMiddle')
          }}</el-link>
          <el-link type="primary" @click="cutFontSize('small')">{{
            $t('serviceLog.fontSizeSmall')
          }}</el-link>
        </div>
      </el-col>
      <el-col :span="14">
        <span>{{ $t('serviceLog.refreshInterval') }}</span>
        <el-select
          v-model="timeInterval"
          :placeholder="$t('serviceLog.pleaseSelect')"
          size="small"
          class="header-select"
          @change="timeChange()"
        >
          <el-option
            v-for="(item, index) in timeIntervalOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="sizeInterval"
          :placeholder="$t('serviceLog.pleaseSelect')"
          size="small"
          class="header-select"
          @change="sizeChange()"
        >
          <el-option
            v-for="(item, index) in sizeIntervalOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="downloadFileName"
          :placeholder="$t('serviceLog.pleaseSelect')"
          size="small"
        >
          <el-option
            v-for="(item, index) in downloadFiles"
            :key="index"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" size="small" @click="downloadFile">{{
          $t('serviceLog.download')
        }}</el-button>
      </el-col>
    </el-row>
    <el-row>
      <pre v-if="logs.length > 0" :class="logCls">
        {{ logs }}
      </pre>
      <h6 v-else>{{ $t('serviceLog.noData') }}</h6>
    </el-row>
  </div>
</template>

<script>
import { saveAs } from 'file-saver';
import { getServerLog, getLogFiles, downloadFile } from '@/api/console';

export default {
  name: 'OperationLog',
  data() {
    return {
      logs: '',
      downloadFileName: null,
      downloadFiles: [],
      timeInterval: 'manual',
      sizeInterval: 25600,
      timerLog: null,
      fontSize: {
        isBig: true,
        isMiddle: false,
        isSmall: false,
      },
    };
  },
  computed: {
    logCls() {
      return {
        'font-size-big': this.fontSize.isBig,
        'font-size-middle': this.fontSize.isMiddle,
        'font-size-small': this.fontSize.isSmall,
      };
    },
    timeIntervalOptions() {
      return ([
        {
          label: this.$t('serviceLog.manualRefresh'),
          value: 'manual',
        },
        {
          label: `20 ${this.$t('serviceLog.second')}`,
          value: 20000,
        },
        {
          label: `1 ${this.$t('serviceLog.minute')}`,
          value: 60000,
        },
        {
          label: `2 ${this.$t('serviceLog.minute')}`,
          value: 120000,
        },
        {
          label: `5 ${this.$t('serviceLog.minute')}`,
          value: 300000,
        },
      ]);
    },
    sizeIntervalOptions() {
      return ([
        {
          label: `${this.$t('serviceLog.recently')} 25KB`,
          value: 25600,
        },
        {
          label: `${this.$t('serviceLog.recently')} 50KB`,
          value: 51200,
        },
        {
          label: `${this.$t('serviceLog.recently')} 100KB`,
          value: 102400,
        },
      ]);
    },
  },
  created() {
    this.getServerLog();
    this.getLogFiles();
  },
  methods: {
    /**
     * 设置日志字体大小
     */
    cutFontSize(size) {
      switch (size) {
        case 'big':
          this.fontSize.isBig = true;
          this.fontSize.isMiddle = false;
          this.fontSize.isSmall = false;
          break;
        case 'middle':
          this.fontSize.isBig = false;
          this.fontSize.isMiddle = true;
          this.fontSize.isSmall = false;
          break;
        case 'small':
          this.fontSize.isBig = false;
          this.fontSize.isMiddle = false;
          this.fontSize.isSmall = true;
          break;
      }
    },
    /**
     * 获取服务端日志
     */
    async getServerLog() {
      const params = { bytesCount: this.sizeInterval };
      const res = await getServerLog(params);
      this.logs = res.data;
      if (this.timeInterval !== 'manual') {
        this.timerLog = setTimeout(this.getServerLog, this.timeInterval);
      }
    },
    /**
     * 获取可下载日志
     */
    async getLogFiles() {
      const res = await getLogFiles();
      this.downloadFiles = res.data;
    },
    /**
     * 下载日志文件
     */
    async downloadFile() {
      if (!this.downloadFileName) {
        this.$message(this.$t('serviceLog.selectLogFile'));
        return;
      }
      const params = { filename: this.downloadFileName };
      const res = await downloadFile(params);
      saveAs(new Blob([res.data]), this.downloadFileName);
      this.$message({
        message: this.$t('serviceLog.downloadSuccess'),
        type: 'success',
      });
    },
    /**
     * 更新日志获取时间获取间隔
     */
    timeChange() {
      if (this.timerLog) {
        clearTimeout(this.timerLog);
      }
      this.getServerLog();
    },
    /**
     * 更新日志获取大小
     */
    sizeChange() {
      this.getServerLog();
    },
  },
};
</script>

<style scoped>
h6 {
  font-size: 20px;
  text-align: center;
}
pre {
  height: calc(85vh - 190px) !important;
  color: #fff;
  background: #000;
  margin-top: 0;
  margin-bottom: 1rem;
  overflow: auto;
}
.runtime-log {
  border: 20px solid #ebeef5;
  padding: 30px 20px 20px 20px;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
.el-row:first-child {
  margin-bottom: 15px;
}
.el-row:first-child > .el-col:last-child {
  text-align: right;
}
.header-title {
  font-size: 28px;
}
.header-font {
  height: 32px;
  line-height: 32px;
}
.header-select {
  width: 120px;
}
.font-size-big {
  font-size: 16px;
  line-height: 16px;
}
.font-size-middle {
  font-size: 14px;
  line-height: 14px;
}
.font-size-small {
  font-size: 12px;
  line-height: 12px;
}
</style>
