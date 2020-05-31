<template>
  <div
    class="table-responsive"
    style="background:#e4e5e6;padding: 0px 15px 8px 15px;"
  >
    <div style="background:#fff;padding: 0 10px;">
      <h2 id="logs-page-heading">{{ $t('serviceLog.Journal') }}</h2>

      <div>
        <p>
          {{ $t('serviceLog.share') }} {{ logList.length }}
          {{ $t('serviceLog.strip') }}{{ $t('serviceLog.Journal') }}.
        </p>

        <table class="table table-sm table-striped table-bordered">
          <thead>
            <tr style="width:100%">
              <th style="width:60%">
                <span>{{ $t('serviceLog.name') }}</span>
              </th>
              <th style="width:40%">
                <span>{{ $t('serviceLog.level') }}</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="logger in logList" :key="logger.name" style="width:100%">
              <td style="width:60%">
                <small>{{ logger.name }}</small>
              </td>
              <td style="width:40%">
                <button
                  :class="
                    logger.level === 'TRACE' ? 'btn-primary' : 'btn-light'
                  "
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'TRACE')"
                >
                  TRACE
                </button>
                <button
                  :class="
                    logger.level === 'DEBUG' ? 'btn-success' : 'btn-light'
                  "
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'DEBUG')"
                >
                  DEBUG
                </button>
                <button
                  :class="logger.level === 'INFO' ? 'btn-info' : 'btn-light'"
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'INFO')"
                >
                  INFO
                </button>
                <button
                  :class="logger.level === 'WARN' ? 'btn-warning' : 'btn-light'"
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'WARN')"
                >
                  WARN
                </button>
                <button
                  :class="logger.level === 'ERROR' ? 'btn-danger' : 'btn-light'"
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'ERROR')"
                >
                  ERROR
                </button>
                <button
                  :class="
                    logger.level === 'OFF' ? 'btn-secondary' : 'btn-light'
                  "
                  class="btn btn-sm"
                  @click="updateLevel(logger.name, 'OFF')"
                >
                  OFF
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { getLogsLevel, modifyLogsLevel } from '@/api/console';

export default {
  name: 'ServiceLog',
  data() {
    return {
      logList: [],
      filtered: '',
    };
  },
  created() {
    this.getServiceLog();
  },
  methods: {
    updateLevel(name, level) {
      modifyLogsLevel({ name: name, level: level }).then(res => {
        this.getServiceLog();
      });
    },
    getServiceLog() {
      getLogsLevel({}).then(res => {
        if (res.length !== 0) {
          this.logList = res.data;
        }
      });
    },
  },
};
</script>

<style scoped>
.table-responsive {
  display: flex;
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  -ms-overflow-style: -ms-autohiding-scrollbar;
}
h2 {
  font-size: 2rem;
  font-weight: 300;
  padding-top: 10px;
}
p {
  margin-top: 0;
  margin-bottom: 1rem;
}
.form-control {
  display: block;
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #495057;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  border-radius: 0.15rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}
.table {
  width: 100%;
  max-width: 100%;
  margin-bottom: 1rem;
  background-color: transparent;
  border-collapse: collapse;
  table-layout: fixed;
  word-break: break-all;
  word-wrap: break-word;
}
.table-bordered thead td,
.table-bordered thead th {
  border-bottom-width: 2px;
}
.table-bordered,
.table-bordered td,
.table-bordered th {
  border: 1px solid #dee2e6;
}
.table-responsive > .table-bordered {
  border: 0;
}
.table thead th {
  vertical-align: bottom;
  border-bottom: 2px solid #dee2e6;
}
.table-bordered,
.table-bordered td,
.table-bordered th {
  border: 1px solid #dee2e6;
}
.table-sm td,
.table-sm th {
  padding: 0.3rem;
}
.table td,
.table th {
  padding: 0.5rem;
  vertical-align: middle;
  border-top: 1px solid #dee2e6;
}
th {
  text-align: inherit;
}
.table-striped tbody tr:nth-of-type(odd) {
  background-color: rgba(0, 0, 0, 0.05);
}
small {
  font-size: 80%;
  font-weight: 400;
}
button,
input,
optgroup,
select,
textarea {
  margin: 0;
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}
button,
input {
  overflow: visible;
}
button,
select {
  text-transform: none;
}
[type='reset'],
[type='submit'],
button,
html [type='button'] {
  -webkit-appearance: button;
}
.btn {
  display: inline-block;
  font-weight: 400;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  user-select: none;
  border: 1px solid transparent;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.15rem;
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out,
    border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}
.btn-danger {
  color: #fff;
  background-color: #dc3545;
  border-color: #dc3545;
}
.btn-light {
  color: #212529;
  background-color: #f8f9fa;
  border-color: #f8f9fa;
}
.btn-info {
  color: #fff;
  background-color: #17a2b8;
  border-color: #17a2b8;
}
.btn-warning {
  color: #212529;
  background-color: #ffc107;
  border-color: #ffc107;
}
.btn-primary {
  color: #fff;
  background-color: #3e8acc;
  border-color: #3e8acc;
}
.btn-success {
  color: #fff;
  background-color: #28a745;
  border-color: #28a745;
}

.btn-group-sm > .btn,
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 0.1rem;
}
.btn:not(:disabled):not(.disabled) {
  cursor: pointer;
}
</style>
