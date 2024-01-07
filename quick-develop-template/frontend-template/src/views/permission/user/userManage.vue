<script setup lang="ts">
import { AddUser, DeleteById, GetUserList, UpdateUser } from "@/api/user";
import { message } from "@/utils/message";
import { markRaw, onMounted, Ref, ref } from "vue";
import { ElInput, ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import { UserInfo, UserQueryRequest } from "@/model/user";
import UploadImage from "@/components/common/UploadImage.vue";
import { Page } from "@/model/comon";

defineOptions({
  name: "UserManage"
});

const userQuery: Ref<UserQueryRequest> = ref({
  /**
   * 当前页号
   */
  current: 1,
  /**
   * 页面大小
   */
  pageSize: 10
});

const pageUserList: Ref<Page<UserInfo>> = ref({});

// 初始化用户数据
const getAllUser = async () => {
  const res = await GetUserList(userQuery.value);
  if (res.code === 0) {
    pageUserList.value = res.data;
  } else {
    message(res.message, { type: "error" });
  }
};

// 删除确认框
const deleteUser = id => {
  ElMessageBox.confirm("注意，此操作不可撤销，是否继续？", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
    icon: markRaw(Delete),
    draggable: true
  })
    .then(async () => {
      // 删除用户
      const res = await DeleteById(id);
      if (res.code === 0) {
        message("删除成功", { type: "success" });
        getAllUser();
      } else {
        message(res.message, { type: "error" });
      }
    })
    .catch(() => {
      ElMessage({
        type: "info",
        message: "取消删除"
      });
    });
};

// 编辑用户信息
const editFormVisible = ref(false);
const formLabelWidth = "100px";
// 当前修改用户
const currentUser = ref({} as UserInfo);
// 性别选项
const genderOptions = [
  {
    value: 1,
    label: "男"
  },
  {
    value: 0,
    label: "女"
  }
];
// 修改用户对话框
const editUser = (user: UserInfo) => {
  editFormVisible.value = true;
  currentUser.value = Object.assign({}, user);
};
// 取消修改
const cancelEdit = () => {
  editFormVisible.value = false;
  currentUser.value = {} as UserInfo;
};
// 确认修改
const confirmEdit = async () => {
  const res = await UpdateUser(currentUser.value);
  if (res.code === 0) {
    message("修改成功", { type: "success" });
    getAllUser();
    cancelEdit();
  } else {
    message(res.message, { type: "error" });
  }
};
/**
 * 分页
 */
const handleSizeChange = (val: number) => {
  userQuery.value.pageSize = val;
  userQuery.value.current = 1;
  getAllUser();
};
const handleCurrentChange = (val: number) => {
  userQuery.value.current = val;
  getAllUser();
};

onMounted(() => {
  getAllUser();
});

/**
 * 新增用户
 */
const showAddDialog = ref(false);
const addUserInfo: Ref<UserInfo> = ref({});

const showAdd = () => {
  showAddDialog.value = true;
};

const closeAdd = () => {
  showAddDialog.value = false;
  addUserInfo.value = { id: 0 };
};

const confirmAdd = async () => {
  showAddDialog.value = false;
  const res = await AddUser(addUserInfo.value);
  if (res.code !== 0) {
    return ElMessage.error(res.message);
  }
  addUserInfo.value = { id: 0 };
  ElMessage.success("添加成功");
  await getAllUser();
};
</script>

<template>
  <div>
    <el-card class="box-card">
      <el-row>
        <el-col :span="6" />
        <el-col :span="6" />
        <el-col :span="10" />
        <el-col :span="2">
          <!--      新增按钮-->
          <el-button
            type="success"
            size="large"
            style="font-size: 16px"
            plain
            @click="showAdd"
            >+ 新增用户</el-button
          >
        </el-col>
      </el-row>

      <!--  展示表格-->
      <el-table
        :data="pageUserList?.records"
        style="width: 100%"
        table-layout="auto"
      >
        <el-table-column fixed type="index" :index="1" />
        <el-table-column fixed prop="userName" label="用户名" />
        <el-table-column prop="userAccount" label="账号" />
        <!--    头像-->
        <el-table-column label="头像">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <el-avatar shape="square" :src="scope.row.userAvatar" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="电子邮箱" />
        <el-table-column prop="userProfile" label="个人简介" />
        <!--    性别-->
        <el-table-column label="性别">
          <template #default="scope">
            {{ scope.row.gender === 1 ? "男" : "女" }}
          </template>
        </el-table-column>
        <!--    用户权限-->
        <el-table-column prop="userRole" label="用户角色" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column fixed="right" label="操作">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="small"
              @click="editUser(scope.row)"
              >编辑</el-button
            >
            <el-button
              link
              type="danger"
              size="small"
              @click="deleteUser(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!--  底部分页选择-->
      <el-pagination
        style="margin-top: 15px"
        v-model:current-page="pageUserList.current"
        v-model:page-size="pageUserList.size"
        :page-sizes="[5, 10, 15, 20]"
        :small="false"
        :disabled="false"
        :background="false"
        layout="total, sizes, prev, pager, next, jumper"
        :page-count="parseInt(pageUserList.pages)"
        :total="parseInt(pageUserList.total)"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    <!--  编辑信息对话框-->
    <el-dialog
      v-model="editFormVisible"
      title="编辑用户信息"
      style="width: 580px"
    >
      <el-form :model="currentUser">
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="currentUser.userName" autocomplete="off" />
        </el-form-item>
        <!--      头像修改-->
        <el-form-item label="头像" :label-width="formLabelWidth">
          <!--          自定义组件-->
          <UploadImage v-model:img-url="currentUser.userAvatar" />
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth">
          <el-input v-model="currentUser.userProfile" autocomplete="off" />
        </el-form-item>
        <el-form-item label="个人邮箱" :label-width="formLabelWidth">
          <el-input v-model="currentUser.email" autocomplete="off" />
        </el-form-item>
        <!--      性别选项-->
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-select v-model="currentUser.gender" placeholder="Select">
            <el-option
              v-for="gender in genderOptions"
              :key="gender.value"
              :label="gender.label"
              :value="gender.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="confirmEdit"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>

    <!--    新增用户-->
    <el-dialog
      v-model="showAddDialog"
      title="新增弹窗"
      width="30%"
      center
      draggable
    >
      <el-form
        label-position="right"
        label-width="80px"
        :model="addUserInfo"
        style="max-width: 460px"
      >
        <el-form-item label="用户名">
          <el-input v-model="addUserInfo.userName" />
        </el-form-item>
        <!--      头像修改-->
        <el-form-item label="头像">
          <!--          自定义组件-->
          <UploadImage v-model:img-url="addUserInfo.userAvatar" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="addUserInfo.userAccount" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="addUserInfo.userPassword" />
        </el-form-item>
        <el-form-item label="个人邮箱">
          <el-input v-model="addUserInfo.email" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="addUserInfo.userProfile" />
        </el-form-item>
        <el-form-item label="性别">
          <el-switch
            v-model="addUserInfo.gender"
            style="
              --el-switch-on-color: #99daf8;
              --el-switch-off-color: #f077ac;
            "
            inline-prompt
            active-text="男"
            inactive-text="女"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="addUserInfo.userRole" class="m-2">
            <el-option key="管理员" label="管理员" value="admin" />
            <el-option key="普通用户" label="普通用户" value="user" />
            <el-option key="禁用" label="禁用" value="ban" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeAdd">取消</el-button>
          <el-button type="primary" @click="confirmAdd"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
