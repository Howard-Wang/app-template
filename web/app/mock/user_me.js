function data(req) {
  let res = null;
  if (req.method === 'GET') {
    res = {
      id: '1',
      userId: null,
      userToken: null,
      enterpriseId: '1',
      username: 'admin',
      password: '',
      userRole: 'ROLE_SUPERMAN',
    };
  } else {
    res = {};
  }
  return res;
}

module.exports = data;
