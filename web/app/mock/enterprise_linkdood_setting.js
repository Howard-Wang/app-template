function data(req) {
  let res = null;
  if (req.method === 'GET') {
    res = {
      enterpriseId: '1',
      accessType: 'userID',
    };
  } else {
    res = {};
  }
  return res;
}

module.exports = data;
