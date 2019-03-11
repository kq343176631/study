package com.style.admin.modules.oss.storage;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.utils.core.SpringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * FastDFS
 */
public class FastDFSCloudStorageService extends AbstractCloudStorageService {

    private static DefaultGenerateStorageClient defaultGenerateStorageClient;

    static {
        defaultGenerateStorageClient = (DefaultGenerateStorageClient) SpringUtils.getBean("defaultGenerateStorageClient");
    }

    public FastDFSCloudStorageService(CloudStorageConfig config) {
        this.config = config;
    }

    @Override
    public String upload(byte[] data, String suffix) {
        return upload(new ByteArrayInputStream(data), suffix);
    }

    @Override
    public String upload(InputStream inputStream, String suffix) {

        StorePath storePath;
        try {
            storePath = defaultGenerateStorageClient.uploadFile("group1", inputStream, inputStream.available(), suffix);
        } catch (Exception ex) {
            throw new ValidateException(ErrorCode.OSS_UPLOAD_FILE_ERROR, ex, ex.getMessage());
        }

        return config.getFastdfsDomain() + "/" + storePath.getPath();
    }
}