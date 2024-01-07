package com.guanzhi.springbootinit.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.guanzhi.springbootinit.config.OssClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * oss 对象存储操作
 */
@Component
public class OssManager {

    @Resource
    private OssClientConfig cosClientConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return ossClient.putObject(putObjectRequest);
    }
}
