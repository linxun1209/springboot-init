package com.guanzhi.springbootinit.esdao;

import com.guanzhi.springbootinit.model.dto.post.PostEsDTO;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
}