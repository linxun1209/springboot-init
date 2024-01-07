<template>
  <div class="select-none">
    <img :src="bg" class="wave" />
    <div class="flex-c absolute right-5 top-3">
      <!-- 主题 -->
      <el-switch
        v-model="dataTheme"
        inline-prompt
        :active-icon="dayIcon"
        :inactive-icon="darkIcon"
        @change="dataThemeChange"
      />
    </div>
    <div class="login-container">
      <div class="img">
        <component :is="toRaw(illustration)" />
      </div>
      <div class="login-box">
        <div class="login-form">
          <Motion>
            <h2 class="outline-none">{{ title }}</h2>
          </Motion>
          <!--          登录表单-->
          <Login v-model:current-page="currentPage" v-if="currentPage === 'login'" />
          <!--          注册表单-->
          <Register v-model:current-page="currentPage" v-if="currentPage === 'register'" />
          <!--          找回密码表单-->
          <Forget v-model:current-page="currentPage" v-if="currentPage === 'forget'" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import Motion from "./utils/motion";
import { useNav } from "@/layout/hooks/useNav";
import { useLayout } from "@/layout/hooks/useLayout";
import { bg, illustration } from "./utils/static";
import { onBeforeUnmount, onMounted, ref, toRaw } from "vue";
import { useDataThemeChange } from "@/layout/hooks/useDataThemeChange";
import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import Forget from "@/views/login/components/Forget.vue";
import Register from "@/views/login/components/Register.vue";
import Login from "@/views/login/components/Login.vue";

defineOptions({
  name: "Login"
});

const { initStorage } = useLayout();
initStorage();

const { dataTheme, dataThemeChange } = useDataThemeChange();
dataThemeChange();
const { title } = useNav();

// 切换显示不同的表单
const currentPage = ref("login");

onMounted(() => {
  window.document.addEventListener("keypress", onkeypress);
});

onBeforeUnmount(() => {
  window.document.removeEventListener("keypress", onkeypress);
});
</script>

<style scoped>
@import url("@/style/login.css");

:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}
</style>
