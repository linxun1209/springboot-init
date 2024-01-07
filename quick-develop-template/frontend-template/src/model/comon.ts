/**
 * 获取验证码倒计时
 */
export type LockType = {
  isDisabled: boolean;
  timer: object;
  text: string;
}

/**
 * 分页查询
 */
export type Page<T> = {
  /**
   * 记录列表
   */
  records: Array<T>;
  /**
   * 总条数
   */
  total: number;
  /**
   * 页大小
   */
  size: number;
  /**
   * 当前页
   */
  current: number;
  /**
   * 总页数
   */
  pages: number;
}
