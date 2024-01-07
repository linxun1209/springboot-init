// 发送验证码
import { ref } from "vue";
import { clone } from "@pureadmin/utils";
import { GetMailCode } from "@/api/common";
import { ElMessage } from "element-plus";
import { LockType } from "@/model/comon";

export const start = async (mail: string, time = 60) => {
  const isDisabled = ref(false);
  const timer = ref(null);
  const text = ref("");

  const initTime = clone(time, true);
  clearInterval(timer.value);
  timer.value = setInterval(() => {
    if (time > 0) {
      text.value = `${time}`;
      isDisabled.value = true;
      time -= 1;
    } else {
      text.value = "";
      isDisabled.value = false;
      clearInterval(timer.value);
      time = initTime;
    }
  }, 1000);
  // 发送验证码
  const res = await GetMailCode(mail);
  if (res.code === 0) {
    ElMessage({
      message: res.data,
      type: "success"
    });
  } else {
    ElMessage.error("系统繁忙");
  }
  return { isDisabled, timer, text } as LockType;
};

