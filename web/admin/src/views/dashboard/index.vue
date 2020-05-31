<template>
  <div class="image-box">
    <img class="home-image" src="@/assets/home.jpg">
    <div class="introduction">
      <p class="title">{{ $t('common.title') }}</p>
      <p class="small-title">{{ $t('common.content') }}</p>
    </div>
    <div class="clearfix" />
  </div>
</template>

<script>
export default {};
</script>

<style lang="scss" scoped>
.image-box {
  width: 96%;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0px 1px 12px 0px rgba(40, 40, 40, 0.12);
  border-radius: 4px;
  margin: 30px;
  padding: 30px;
}
.home-image {
  display: block;
  width: 36%;
  border: 1px solid #f4f4f4;
  float: left;
}
.introduction {
  float: left;
  width: 50%;
  margin-left: 28px;
}
.clearfix {
  clear: both;
}
.title{
  font-size: 24px;
  color:#333;
  font-weight: 800;
}
.small-title{
  color:#666;
  line-height: 28px;
}
</style>
