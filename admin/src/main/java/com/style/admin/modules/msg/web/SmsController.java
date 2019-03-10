package com.style.admin.modules.msg.web;

import com.style.admin.common.group.AliyunGroup;
import com.style.admin.common.group.QcloudGroup;
import com.style.admin.modules.msg.config.SmsConfig;
import com.style.admin.modules.msg.service.SysSmsService;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.Constants;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.ValidatorUtils;
import com.style.common.web.WebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Map;

/**
 * 短信服务
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sys/sms")
@Api(tags = "短信服务")
public class SmsController extends WebController {

    @Autowired
    private SysSmsService sysSmsService;

    @Autowired
    private SysParamsService sysParamsService;

    private final static String KEY = Constants.SMS_CONFIG_KEY;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "mobile", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "status", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:sms:all")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(sysSmsService.page(params));
    }

    @GetMapping("/config")
    @ApiOperation("获取配置短信")
    @RequiresPermissions("sys:sms:all")
    public Result<SmsConfig> config() {
        SmsConfig config = sysParamsService.getValueObject(KEY, SmsConfig.class);

        return success(config);
    }

    @PostMapping("/saveConfig")
    @ApiOperation("保存配置短信")
    @RequiresPermissions("sys:sms:all")
    public Result saveConfig(@RequestBody SmsConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getPlatform() == Constants.SmsService.ALIYUN.getValue()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        } else if (config.getPlatform() == Constants.SmsService.QCLOUD.getValue()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(config, QcloudGroup.class);
        }

        sysParamsService.updateValueByCode(KEY, JsonMapper.toJson(config));

        return new Result();
    }

    @PostMapping("/send")
    @ApiOperation("发送短信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机好号", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "params", value = "参数", paramType = "query", required = true, dataType = "String")
    })
    @RequiresPermissions("sys:sms:all")
    public Result send(String mobile, String params) {
        sysSmsService.send(mobile, params);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:sms:all")
    public Result delete(@RequestBody Long[] ids) {

        sysSmsService.deleteByIds(Arrays.asList(ids));

        return new Result();
    }

}