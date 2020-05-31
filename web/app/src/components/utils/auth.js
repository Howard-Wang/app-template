import store from '../../store';

/**
 * 检查当前用户的有没有对应的授权
 */
export function check(authority) {
  const current = [store.getters.userCurrentAuthority];
  return current.some(item => authority.includes(item));
}

export default {
  check,
};
