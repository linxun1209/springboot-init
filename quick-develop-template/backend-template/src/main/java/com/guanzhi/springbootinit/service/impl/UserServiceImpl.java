package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.constant.CommonConstant;
import com.guanzhi.springbootinit.constant.RegConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.user.*;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.enums.UserRoleEnum;
import com.guanzhi.springbootinit.model.vo.LoginUserVO;
import com.guanzhi.springbootinit.model.vo.UserVO;
import com.guanzhi.springbootinit.service.UserService;
import com.guanzhi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.guanzhi.springbootinit.constant.RedisKeyConstant.YZM_PRE;
import static com.guanzhi.springbootinit.constant.UserConstant.SALT;
import static com.guanzhi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 获取参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String mail = userRegisterRequest.getMail();
        String checkCode = userRegisterRequest.getCheckCode();
        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, mail, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }
        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userRegisterRequest, userFormValid);
        validFormValue(userFormValid);

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setEmail(mail);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public boolean resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        String userAccount = userPasswordResetRequest.getUserAccount();
        String userPassword = userPasswordResetRequest.getUserPassword();
        String checkPassword = userPasswordResetRequest.getCheckPassword();
        String mail = userPasswordResetRequest.getMail();
        String checkCode = userPasswordResetRequest.getCheckCode();

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, mail, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }

        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userPasswordResetRequest, userFormValid);
        // 格式校验
        validFormValue(userFormValid);


        // 特殊性校验
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User loginUser = this.getOne(queryWrapper);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 验证邮箱
        if (!loginUser.getEmail().equals(mail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入当前用户邮箱");
        }

        // 修改数据
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserPassword(encryptPassword);
        return this.updateById(user);
    }


    @Override
    public boolean updatePassword(UserPasswordUpdateRequest userPasswordUpdateRequest, HttpServletRequest request) {
        String oldPassword = userPasswordUpdateRequest.getOldPassword();
        String userPassword = userPasswordUpdateRequest.getUserPassword();
        String checkPassword = userPasswordUpdateRequest.getCheckPassword();
        String mail = userPasswordUpdateRequest.getMail();
        String checkCode = userPasswordUpdateRequest.getCheckCode();

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(oldPassword, userPassword, checkPassword, mail, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }

        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userPasswordUpdateRequest, userFormValid);
        // 格式校验
        validFormValue(userFormValid);

        // 获取当前用户
        User loginUser = this.getById(this.getLoginUser(request).getId());
        // 判断旧密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if (!loginUser.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确旧密码");
        }
        // 验证邮箱
        if (!loginUser.getEmail().equals(mail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入当前用户邮箱");
        }
        // 修改数据
        encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserPassword(encryptPassword);
        return this.updateById(user);
    }


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }
        UserFormValid userFormValid = new UserFormValid();
        userFormValid.setUserAccount(userAccount);
        userFormValid.setUserPassword(userPassword);
        // 格式校验
        validFormValue(userFormValid);

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 验证登录注册表单字段格式是否符合要求
     * 只校验传递的参数，不校验参数是否为必填项
     *
     * @return
     */
    public void validFormValue(UserFormValid userFormValid) {
        String userAccount = userFormValid.getUserAccount();
        String userPassword = userFormValid.getUserPassword();
        String checkPassword = userFormValid.getCheckPassword();
        String mail = userFormValid.getMail();
        String checkCode = userFormValid.getCheckCode();

        // 账号校验
        if (userAccount != null) {
            if (userAccount.length() < 4 || userAccount.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号必须为4~16位");
            }
            boolean isMatches = Pattern.matches(RegConstant.ACCOUNT_REG, userAccount);
            if (!isMatches) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能含特殊字符");
            }
        }

        // 密码校验
        if (userPassword != null) {
            if (userPassword.length() < 8 || userPassword.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码必须为8~16位");
            }
            boolean isMatches = Pattern.matches(RegConstant.PASSWORD_REG, userPassword);
            if (!isMatches) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能含特殊字符");
            }
        }

        // 确认密码校验
        if (checkPassword != null) {
            // 密码和校验密码相同
            if (!checkPassword.equals(userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
            }
        }

        // 校验邮箱
        if (mail != null) {
            boolean isMatch = Pattern.matches(RegConstant.EMAIL_REG, mail);
            if (!isMatch) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确邮箱");
            }
        }

        // 校验验证码,在邮箱不为null的情况下校验
        if (checkCode != null && mail != null) {
            String code = redisTemplate.opsForValue().get(YZM_PRE + mail);
            if (code == null || code.length() != 6) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新获取验证码");
            }
            if (!checkCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
        }
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        Integer gender = userQueryRequest.getGender();
        String email = userQueryRequest.getEmail();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(gender != null, "gender", gender);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
