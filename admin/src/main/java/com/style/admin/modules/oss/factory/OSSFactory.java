package com.style.admin.modules.oss.factory;

import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.admin.modules.oss.storage.*;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.Constants;
import com.style.utils.core.SpringUtils;

/**
 * 文件上传Factory
 */
public final class OSSFactory {

    private static SysParamsService sysParamsService;

    static {
        OSSFactory.sysParamsService = SpringUtils.getBean(SysParamsService.class);
    }

    public static AbstractCloudStorageService build() {
        //获取云存储配置信息
        CloudStorageConfig config = sysParamsService.getValueObject(Constants.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);

        if (config.getType() == Constants.CloudService.QINIU.getValue()) {
            return new QiniuCloudStorageService(config);
        } else if (config.getType() == Constants.CloudService.ALIYUN.getValue()) {
            return new AliyunCloudStorageService(config);
        } else if (config.getType() == Constants.CloudService.QCLOUD.getValue()) {
            return new QcloudCloudStorageService(config);
        } else if (config.getType() == Constants.CloudService.FASTDFS.getValue()) {
            return new FastDFSCloudStorageService(config);
        } else if (config.getType() == Constants.CloudService.LOCAL.getValue()) {
            return new LocalCloudStorageService(config);
        }

        return null;
    }

}