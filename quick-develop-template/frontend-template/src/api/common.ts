import { http } from "@/utils/http";
import { BaseResponse } from "@/model/BaseResponse";

/** 获取邮箱验证码 */
export const GetMailCode = async (targetEmail: string) => {
  return await http.post<string, BaseResponse<string>>("/common/getMailCode", {
    data: targetEmail
  });
};

/** 上传图片 */
export const UploadImage = async (forms: FormData) => {
  return await http.post<FormData, BaseResponse<string>>(
    "/file/upload",
    { data: forms },
    {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    }
  );
};
