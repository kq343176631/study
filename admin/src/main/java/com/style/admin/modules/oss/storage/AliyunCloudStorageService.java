package com.style.admin.modules.oss.storage;

import com.aliyun.oss.OSSClient;
import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云存储
 */
public class AliyunCloudStorageService extends AbstractCloudStorageService {

    public AliyunCloudStorageService(CloudStorageConfig config) {
        this.config = config;
    }

    @Override
    public String upload(byte[] data, String suffix) {
        return upload(new ByteArrayInputStream(data), suffix);
    }

    @Override
    public String upload(InputStream inputStream, String suffix) {

        String path = getPath(config.getAliyunPrefix(), suffix);

        OSSClient client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);
            client.shutdown();
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }

        return config.getAliyunDomain() + "/" + path;
    }
}