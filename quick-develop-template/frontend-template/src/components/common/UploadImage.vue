<template>
  <el-upload
    class="avatar-uploader"
    :show-file-list="false"
    :before-upload="beforeAvatarUpload"
    :http-request="updateUserAvatar"
  >
    <img v-if="imgUrl" :src="imgUrl" class="avatar" alt="修改头像" />
    <el-icon v-else class="avatar-uploader-icon">
      <Plus />
    </el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { UploadImage } from "@/api/common.ts";
import { Plus } from "@element-plus/icons-vue";

defineProps(["imgUrl"]);
const $emit = defineEmits(["update:imgUrl"]);

// 校验文件格式
const fileType = ["image/jpeg", "image/png"];
const beforeAvatarUpload = (rawFile) => {
  if (!fileType.includes(rawFile.type)) {
    ElMessage.error("头像必须为JPG/PNG格式!");
    return false;
  } else if (rawFile.size / 1024 / 1024 > 3) {
    ElMessage.error("文件大小不能超过3MB!");
    return false;
  }
  return true;
};
// 修改头像
const updateUserAvatar = async (rawFile) => {
  const forms: FormData = new FormData();
  //这里的file表示给后台传的属性名字，这里看接口文档需要传的的属性
  forms.append("file", rawFile?.file); // 获取上传图片信息
  forms.append("biz", "user_avatar");
  const res = await UploadImage(forms);
  if (res.code === 0) {
    // 传递给父亲
    $emit("update:imgUrl", res.data);
    ElMessage.success("修改成功");
  } else {
    ElMessage.error(res.message);
  }
};

defineOptions({
  name: "uploadAvatar"
});

</script>

<style scoped>
.avatar-uploader .avatar {
  width: 128px;
  height: 128px;
  display: block;
}

.avatar-uploader .avatar-uploader-icon {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .avatar-uploader-icon:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
