package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.User;

/**
 * 帖子点赞服务
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
