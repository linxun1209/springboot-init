import { http } from "@/utils/http";
import { BaseResponse } from "@/model/BaseResponse";
import { UserInfo, UserQueryRequest } from "@/model/user";
import { Page } from "@/model/comon";

/** 登录 */
export const GetLogin = async (data: object) => {
  return await http.post<object, BaseResponse<UserInfo>>("/user/login", { data });
};

/** 退出登录 */
export const GetLogout = async () => {
  return await http.post<null, BaseResponse<boolean>>("/user/logout");
};


/** 注册 */
export const GetRegister = async (data: object) => {
  return await http.post<object, BaseResponse<number>>("/user/register", { data });
};

/** 忘记密码 */
export const ResetPassword = async (data: object) => {
  return await http.post<object, BaseResponse<boolean>>("/user/password/reset", { data });
};

/** 修改密码 */
export const UpdatePassword = async (data: object) => {
  return await http.post<object, BaseResponse<boolean>>("/user/password/update", { data });
};

/** 修改个人信息 */
export const UpdateMyInfo = async (data: UserInfo) => {
  return await http.post<UserInfo, BaseResponse<boolean>>("/user/update/my", { data });
};


// admin start
/** 分页查询获取全部用户*/
export const GetUserList = async (userQueryRequest: UserQueryRequest) => {
  return await http.post<UserQueryRequest, BaseResponse<Page<UserInfo>>>("/user/list/page", { data: userQueryRequest });
};

/** 根据id删除用户*/
export const DeleteById = async (id: number) => {
  return await http.post<number, BaseResponse<boolean>>("/user/delete", { data: { id } });
};

/** 修改用户信息*/
export const UpdateUser = async (data: UserInfo) => {
  return await http.post<UserInfo, BaseResponse<boolean>>("/user/update", { data });
};


/** 新增用户 */
export const AddUser = async (user: UserInfo) => {
  return await http.post<UserInfo, BaseResponse<number>>("/user/add", { data: user });
};

// admin end


