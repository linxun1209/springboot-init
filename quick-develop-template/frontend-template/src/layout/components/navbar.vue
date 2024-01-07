<script setup lang="ts">
import Search from "./search/index.vue";
import Notice from "./notice/index.vue";
import { useNav } from "@/layout/hooks/useNav";
import Breadcrumb from "./sidebar/breadCrumb.vue";
import LogoutCircleRLine from "@iconify-icons/ri/logout-circle-r-line";
import Setting from "@iconify-icons/ri/settings-3-line";
import triangleAlert from "@iconify-icons/akar-icons/triangle-alert";
import avatarIcon from "@iconify-icons/ep/avatar";
import { ref } from "vue";
import UpdatePassword from "@/views/login/components/UpdatePassword.vue";
import router from "@/router";

const {
  layout,
  device,
  logout,
  onPanel,
  pureApp,
  user,
  avatarsStyle,
  toggleSideBar
} = useNav();

// 修改密码
const dialogFormVisible = ref(false);
const toUpdatePassword = () => {
  dialogFormVisible.value = true;
};

// 跳转到用户登录页
const toMyInfoPage = () => {
  router.push("/user/myInfo");
};
</script>
<template>
  <div
    class="navbar bg-[#fff] shadow-sm shadow-[rgba(0, 21, 41, 0.08)] dark:shadow-[#0d0d0d]"
  >
    <topCollapse
      v-if="device === 'mobile'"
      class="hamburger-container"
      :is-active="pureApp.sidebar.opened"
      @toggleClick="toggleSideBar"
    />

    <Breadcrumb
      v-if="layout !== 'mix' && device !== 'mobile'"
      class="breadcrumb-container"
    />

    <mixNav v-if="layout === 'mix'" />

    <div v-if="layout === 'vertical'" class="vertical-header-right">
      <!-- 菜单搜索 -->
      <Search />
      <!-- 通知 -->
      <Notice id="header-notice" />
      <!-- Todo 顶部Nav-->
      <!-- 个人下拉选项 -->
      <el-dropdown trigger="click">
        <span class="el-dropdown-link navbar-bg-hover select-none">
          <img :src="user.userAvatar" :style="avatarsStyle" />
          <p class="dark:text-white">{{ user.userName + ",欢迎您！" }}</p>
        </span>
        <template #dropdown>
          <!--          个人中心-->
          <el-dropdown-menu class="nav_item">
            <el-dropdown-item @click="toMyInfoPage">
              <IconifyIconOffline
                :icon="avatarIcon"
                style="margin: 5px"
              />
              个人中心
            </el-dropdown-item>
          </el-dropdown-menu>
          <!--          修改密码-->
          <el-dropdown-menu class="nav_item">
            <el-dropdown-item @click="toUpdatePassword">
              <IconifyIconOffline
                :icon="triangleAlert"
                style="margin: 5px"
              />
              修改密码
            </el-dropdown-item>
          </el-dropdown-menu>
          <!--          退出登录-->
          <el-dropdown-menu class="nav_item">
            <el-dropdown-item @click="logout">
              <IconifyIconOffline
                :icon="LogoutCircleRLine"
                style="margin: 5px"
              />
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <span
        class="set-icon navbar-bg-hover"
        title="打开项目配置"
        @click="onPanel"
      >
        <IconifyIconOffline :icon="Setting" />
      </span>
    </div>


    <!--    修改密码-->
    <el-dialog width="30%" draggable center destroy-on-close v-model="dialogFormVisible" title="修改密码">
      <UpdatePassword v-model:dialogFormVisible="dialogFormVisible" />
    </el-dialog>

  </div>
</template>

<style lang="scss" scoped>
.navbar {
  width: 100%;
  height: 48px;
  overflow: hidden;

  .hamburger-container {
    float: left;
    height: 100%;
    line-height: 48px;
    cursor: pointer;
  }

  .vertical-header-right {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    min-width: 280px;
    height: 48px;
    color: #000000d9;

    .el-dropdown-link {
      display: flex;
      align-items: center;
      justify-content: space-around;
      height: 48px;
      padding: 10px;
      color: #000000d9;
      cursor: pointer;

      p {
        font-size: 14px;
      }

      img {
        width: 22px;
        height: 22px;
        border-radius: 50%;
      }
    }
  }

  .breadcrumb-container {
    float: left;
    margin-left: 16px;
  }
}

.nav_item {
  max-width: 120px;

  ::v-deep(.el-dropdown-menu__item) {
    display: inline-flex;
    flex-wrap: wrap;
    min-width: 100%;
  }
}
</style>
