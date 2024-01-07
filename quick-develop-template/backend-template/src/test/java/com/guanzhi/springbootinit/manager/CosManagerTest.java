package com.guanzhi.springbootinit.manager;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cos 操作测试
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private OssManager cosManager;

    @Test
    void putObject() {
        cosManager.putObject("test", "test.json");
    }
}