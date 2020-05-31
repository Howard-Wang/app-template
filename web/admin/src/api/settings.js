import request from '@/utils/request';
export function getManagerInfo(data) {
  return request({
    url: '/admin/accountQuery',
    method: 'post',
    data,
  });
}

