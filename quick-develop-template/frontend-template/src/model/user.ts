export type UserInfo = {
  /**
   * id
   */
  id: number;
  /**
   * 用户账号
   */
  userAccount?: string;
  /**
   * 用户密码
   */
  userPassword?: string;
  /**
   * 开放平台id
   */
  unionId?: string;
  /**
   * 公众号openId
   */
  mpOpenId?: string;
  /**
   * 用户昵称
   */
  userName?: string;
  /**
   * 用户邮箱
   */
  email?: string;
  /**
   * 用户性别
   */
  gender?: number;
  /**
   * 用户头像
   */
  userAvatar?: string;
  /**
   * 用户简介
   */
  userProfile?: string;
  /**
   * 用户角色：user/admin/ban
   */
  userRole?: string;
  /**
   * 创建时间
   */
  createTime?: Date;
  /**
   * 更新时间
   */
  updateTime?: Date;
};


export type UserQueryRequest = {
  /**
   * id
   */
  id?: number;
  /**
   * 用户昵称
   */
  userName?: string;
  /**
   * 用户性别
   */
  gender?: number;
  /**
   * 用户邮箱
   */
  email?: string;
  /**
   * 简介
   */
  userProfile?: string;
  /**
   * 用户角色：user/admin/ban
   */
  userRole?: String;
  /**
   * 排序字段
   */
  sortField?: string;
  /**
   * 排序顺序（默认升序）
   */
  sortOrder?: string;
  /**
   * 当前页号
   */
  current: number;
  /**
   * 页面大小
   */
  pageSize: number;
}
