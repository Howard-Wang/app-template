import request from '@/utils/request';
import Qs from 'qs';

export function getLogsLevel() {
  return request({
    url: '/api/logs/level',
    method: 'get',
  });
}

export function modifyLogsLevel(data) {
  return request({
    url: '/api/logs/level',
    method: 'put',
    data,
  });
}

export function getServerLog(data) {
  return request({
    url: '/api/logs/server-log',
    method: 'get',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function getLogFiles() {
  return request({
    url: '/api/logs/files',
    method: 'get',
  });
}

export function downloadFile(data) {
  return request({
    url: '/api/logs/files/download',
    method: 'get',
    responseType: 'blob',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function getClientLog(data) {
  return request({
    url: '/api/logs/web-logs',
    method: 'get',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function removeClientLog(data) {
  return request({
    url: '/api/logs/web-logs',
    method: 'delete',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function downloadClientLog(data) {
  return request({
    url: '/api/logs/web/download',
    method: 'get',
    responseType: 'blob',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function getVersions() {
  return request({
    url: '/api/system/version',
    method: 'get',
  });
}

export default {
  downloadFile,
  getClientLog,
  removeClientLog,
  downloadClientLog,
  getVersions,
};
