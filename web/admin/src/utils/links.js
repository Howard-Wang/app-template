/**
 * 参考 https://github.com/jhipster/ng-jhipster/blob/master/src/service/parse-links.service.ts
 */
export function parseLinks(header) {
  if (header.length === 0) {
    throw new Error('input must not be of zero length');
  }

  // Split parts by comma
  const parts = header.split(',');
  const links = {};

  // Parse each part into a named link
  parts.forEach((p) => {
    const section = p.split(';');

    if (section.length !== 2) {
      throw new Error('section could not be split on ";"');
    }

    const url = section[0].replace(/<(.*)>/, '$1').trim();
    const queryString = {};

    url.replace(
      new RegExp('([^?=&]+)(=([^&]*))?', 'g'),
      ($0, $1, $2, $3) => (queryString[$1] = $3),
    );

    let page = queryString.page;

    if (typeof page === 'string') {
      page = parseInt(page, 10);
    }

    const name = section[1].replace(/rel="(.*)"/, '$1').trim();
    links[name] = page;
  });
  return links;
}

export function getAuthorityText(role) {
  switch (role) {
    case 'USER':
      return '普通用户';
    case 'A_MANAGER':
      return '高级管理员';
    case 'MANAGER':
      return '管理员';
    case 'SUPERMAN':
      return '超级管理员';
    default:
      return '未知权限';
  }
}

export function getExpireWarnText(num) {
  if (num === 0) {
    return '今天到期';
  } else if (num < 0) {
    return '已过期';
  }
  return `${num}天后到期`;
}

export function showExpireWarn(remainingDays) {
  if (remainingDays !== null) {
    return remainingDays < 10; // 10 天内报警
  }
  return false;
}

// 获取当前年月日和往前推一个月的时间
export function getDate() {
  const nowDate = new Date();
  const date = {
    endTime: '',
    startTime: '',
  };
  nowDate.setTime(nowDate.getTime());
  date.endTime =
    nowDate.getFullYear() +
    '-' +
    '0' +
    (nowDate.getMonth() + 1) +
    '-' +
    nowDate.getDate();
  // 开始时间往前推一个月
  nowDate.setMonth(nowDate.getMonth() - 1);
  date.startTime =
    nowDate.getFullYear() +
    '-' +
    '0' +
    (nowDate.getMonth() + 1) +
    '-' +
    nowDate.getDate();
  return date;
}

export default {
  parseLinks,
  getAuthorityText,
  getExpireWarnText,
  showExpireWarn,
  getDate,
};
