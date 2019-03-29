package com.style.admin.modules.oss.storage;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云存储
 */
public class QiniuCloudStorageService extends AbstractCloudStorageService {

    private UploadManager uploadManager;

    private String token;

    public QiniuCloudStorageService(CloudStorageConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).
                uploadToken(config.getQiniuBucketName());

    }

    @Override
    public String upload(byte[] data, String suffix) {
        String path = getPath(config.getQiniuPrefix(), suffix);
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, res.toString());
            }
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }

        return config.getQiniuDomain() + "/" + path;
    }

    @Override
    public String upload(InputStream inputStream, String suffix) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, suffix);
        } catch (IOException e) {
            throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }
    }
}
