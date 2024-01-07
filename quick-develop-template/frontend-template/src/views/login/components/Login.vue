<script setup lang="ts">
import { ref } from "vue";
import Motion from "../utils/motion";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Lock from "@iconify-icons/ri/lock-fill";
import User from "@iconify-icons/ri/user-3-fill";
import { ElMessage, FormInstance } from "element-plus";
import { usePermissionStoreHook } from "@/store/modules/permission";
import { addPathMatch } from "@/router/utils";
import { setToken } from "@/utils/auth";
import { message } from "@/utils/message";
import router from "@/router";
import { GetLogin } from "@/api/user";
import { formRules } from "../utils/rule";
import { userType } from "@/store/modules/types";


const text = ref("");
// 页面切换
const props = defineProps(["currentPage"]);
const $emit = defineEmits(["update:currentPage"]);

// 切换显示不同的表单
const changePage = pageName => {
  $emit("update:currentPage", pageName);
};

const loginForm = ref({
  userAccount: "123456",
  userPassword: "123456789"
});
const loading = ref(false);
const ruleFormRef = ref<FormInstance>();

const onLogin = async (formEl: FormInstance | undefined) => {
  loading.value = true;
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      const res = await GetLogin(loginForm.value);
      if (res.code !== 0) {
        ElMessage.error(res.message);
        loading.value = false;
        return;
      }
      setToken({
        user: res.data,
        roles: [res.data.userRole]
      } as userType);
      router.push({ name: "Home" });
      // 全部采取静态路由模式
      usePermissionStoreHook().handleWholeMenus([]);
      addPathMatch();
      message("登录成功", { type: "success" });
    } else {
      loading.value = false;
      return fields;
    }
  })
}

/** 使用公共函数，避免`removeEventListener`失效 */
function onkeypress({ code }: KeyboardEvent) {
  if (code === "Enter") {
    onLogin(loginForm.value);
  }
}


</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="loginForm"
    :rules="formRules"
    size="large"
    v-if="currentPage === 'login'"
  >
    <Motion :delay="100">
      <el-form-item
        :rules="[
                  {
                    required: true,
                    message: '请输入账号',
                    trigger: 'blur'
                  }
                ]"
        prop="userAccount"
      >
        <el-input
          clearable
          v-model="loginForm.userAccount"
          placeholder="账号"
          :prefix-icon="useRenderIcon(User)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="150">
      <el-form-item prop="userPassword">
        <el-input
          clearable
          show-password
          v-model="loginForm.userPassword"
          placeholder="密码"
          :prefix-icon="useRenderIcon(Lock)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="250">
      <div class="w-full h-[20px] flex justify-between items-center">
        <el-button link type="primary" @click="changePage('forget')">
          忘记密码?
        </el-button>
        <el-button link type="primary" @click="changePage('register')">
          没有账号？立即注册
        </el-button>
      </div>
      <el-button
        class="w-full mt-4"
        size="default"
        type="primary"
        :loading="loading"
        @click="onLogin(ruleFormRef)"
      >
        登录
      </el-button>
    </Motion>
  </el-form>
</template>
