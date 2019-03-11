package com.style.admin.modules.oss.storage;

import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.utils.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地上传
 */
public class LocalCloudStorageService extends AbstractCloudStorageService {

    public LocalCloudStorageService(CloudStorageConfig config) {
        this.config = config;
    }

    @Override
    public String upload(byte[] data, String suffix) {
        return upload(new ByteArrayInputStream(data), suffix);
    }

    @Override
    public String upload(InputStream inputStream, String suffix) {
        String path = getPath(config.getLocalPrefix(), suffix);
        File file = new File(config.getLocalPath() + File.separator + path);
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }
        return config.getLocalDomain() + "/" + path;
    }
}
