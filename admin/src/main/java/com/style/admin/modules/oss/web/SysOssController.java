package com.style.admin.modules.oss.web;

import com.google.gson.Gson;
import com.style.admin.common.group.AliyunGroup;
import com.style.admin.common.group.QcloudGroup;
import com.style.admin.common.group.QiniuGroup;
import com.style.admin.modules.oss.config.CloudStorageConfig;
import com.style.admin.modules.oss.entity.SysOss;
import com.style.admin.modules.oss.factory.OSSFactory;
import com.style.admin.modules.oss.service.SysOssService;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.ValidatorUtils;
import com.style.common.web.WebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传
 */
@RestController
@RequestMapping("sys/oss")
@Api(tags = "文件上传")
public class SysOssController extends WebController {

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private SysParamsService sysParamsService;

    private final static String KEY = Constants.CLOUD_STORAGE_CONFIG_KEY;

    @GetMapping("page")
    @ApiOperation(value = "分页")
    @RequiresPermissions("sys:oss:all")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(sysOssService.page(params));
    }

    @GetMapping("info")
    @ApiOperation(value = "云存储配置信息")
    @RequiresPermissions("sys:oss:all")
    public Result<CloudStorageConfig> info() {

        CloudStorageConfig config = sysParamsService.getValueObject(KEY, CloudStorageConfig.class);

        return success(config);
    }

    @PostMapping
    @ApiOperation(value = "保存云存储配置信息")
    @RequiresPermissions("sys:oss:all")
    public Result saveConfig(@RequestBody CloudStorageConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getType() == Constants.CloudService.QINIU.getValue()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(config, QiniuGroup.class);
        } else if (config.getType() == Constants.CloudService.ALIYUN.getValue()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        } else if (config.getType() == Constants.CloudService.QCLOUD.getValue()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(config, QcloudGroup.class);
        }

        sysParamsService.updateValueByCode(KEY, new Gson().toJson(config));

        return new Result();
    }

    @PostMapping("upload")
    @ApiOperation(value = "上传文件")
    @RequiresPermissions("sys:oss:all")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return error(ErrorCode.UPLOAD_FILE_EMPTY);
        }

        //上传文件
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);

        //保存文件信息
        SysOss ossEntity = new SysOss();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.save(ossEntity);

        Map<String, Object> data = new HashMap<>(1);
        data.put("src", url);

        return success(data);
    }

    @DeleteMapping
    @ApiOperation(value = "删除")
    @RequiresPermissions("sys:oss:all")
    public Result delete(@RequestBody Long[] ids) {

        sysOssService.deleteByIds(Arrays.asList(ids));

        return new Result();
    }

}