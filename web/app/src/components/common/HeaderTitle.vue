<template>
  <div slot="overwrite-title" @click.stop="showConsole">
    {{ title }}
  </div>
</template>

<script>
export default {
  name: 'HeaderTitle',
  props: {
    title: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      count: 0,
      lastClickTime: 0,
    };
  },
  methods: {
    showConsole() {
      const nowTime = new Date().getTime();
      if (this.count === 0) {
        this.lastClickTime = nowTime;
        this.count++;
      } else if (nowTime - this.lastClickTime < 3000) {
        this.lastClickTime = nowTime;
        this.count++;
      } else {
        this.count = 0;
      }

      if (this.count >= 6) {
        const element = document.getElementById('__vconsole');
        element.style.display = 'block';
      }
    },
  },
};
</script>
