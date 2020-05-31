import request from '@/utils/request';
import Qs from 'qs';

export function login(data) {
  return request({
    url: '/api/login',
    method: 'post',
    data: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  });
}

export function getInfo() {
  return request({
    url: '/api/user/me',
    method: 'get',
  });
}

export function logout() {
  return request({
    url: '/api/logout',
    method: 'post',
  });
}

export function getUser(data) {
  return request({
    url: '/api/user',
    method: 'get',
    params: data,
    paramsSerializer: (params) => {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  });
}

export function getUserById(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'get',
  });
}

export function saveUser(data) {
  return request({
    url: '/api/user',
    method: 'post',
    data,
  });
}

export function modifyUser(id, data) {
  return request({
    url: `/api/user/${id}`,
    method: 'patch',
    data,
  });
}

export function removeUser(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'delete',
  });
}

export function resetPwd(id) {
  return request({
    url: `/api/user/reset/${id}`,
    method: 'patch',
  });
}

export default {
  login,
  logout,
  getInfo,
  getUser,
  getUserById,
  saveUser,
  modifyUser,
  removeUser,
  resetPwd,
};
