<script>
/**
 * 该组件用来做权限控制，控制其他组件是否在页面上展示
 */
import store from '@/store';

function check(authority) {
  const current = store.getters.roles;
  return current.some(item => authority.includes(item));
}

export default {
  functional: true, // 函数式组件
  props: {
    authority: {
      type: Array,
      required: true,
    },
  },
  render(h, context) {
    const { props, scopedSlots } = context;
    return check(props.authority) ? scopedSlots.default() : null;
  },
};
</script>
