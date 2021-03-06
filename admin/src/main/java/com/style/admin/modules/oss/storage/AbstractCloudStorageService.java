package com.style.admin.modules.oss.storage;

import com.style.admin.modules.oss.config.CloudStorageConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.DateUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 云存储(支持七牛、阿里云、腾讯云、又拍云)
 */
public abstract class AbstractCloudStorageService {

    /**
     * 云存储配置信息
     */
    CloudStorageConfig config;

    /**
     * 根据文件前缀与后缀自动生成文件存储路径。
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }

        return path + "." + suffix;
    }

    /**
     * 文件上传
     *
     * @param data   文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract String upload(byte[] data, String suffix);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix      后缀
     * @return 返回http地址
     */
    public abstract String upload(InputStream inputStream, String suffix);

}
