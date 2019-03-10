package com.style.admin.modules.msg.web;

import com.style.admin.modules.msg.config.EmailConfig;
import com.style.admin.modules.msg.entity.SysMailTemplateEntity;
import com.style.admin.modules.msg.service.SysMailTemplateService;
import com.style.admin.modules.sys.service.SysParamsService;
import com.style.common.constant.Constants;
import com.style.common.convert.http.json.JsonMapper;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.ValidatorUtils;
import com.style.common.validator.group.AddGroup;
import com.style.common.validator.group.UpdateGroup;
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
 * 邮件模板
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sys/mailtemplate")
@Api(tags = "邮件模板")
public class MailTemplateController extends WebController {

    @Autowired
    private SysMailTemplateService sysMailTemplateService;

    @Autowired
    private SysParamsService sysParamsService;

    private final static String KEY = Constants.MAIL_CONFIG_KEY;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "name", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:mail:all")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(sysMailTemplateService.page(params));
    }

    @GetMapping("/config")
    @ApiOperation("获取配置信息")
    @RequiresPermissions("sys:mail:all")
    public Result<EmailConfig> config() {
        EmailConfig config = sysParamsService.getValueObject(KEY, EmailConfig.class);

        return success(config);
    }

    @PostMapping("/saveConfig")
    @ApiOperation("保存配置信息")
    @RequiresPermissions("sys:mail:all")
    public Result saveConfig(@RequestBody EmailConfig config) {
        //校验数据
        ValidatorUtils.validateEntity(config);

        sysParamsService.updateValueByCode(KEY, JsonMapper.toJson(config));

        return new Result();
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:mail:all")
    public Result<SysMailTemplateEntity> info(@PathVariable("id") Long id) {

        return success(sysMailTemplateService.get(id));
    }

    @PostMapping
    @ApiOperation("保存")
    @RequiresPermissions("sys:mail:all")
    public Result save(SysMailTemplateEntity dto) {
        //校验类型
        ValidatorUtils.validateEntity(dto, AddGroup.class);

        sysMailTemplateService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @RequiresPermissions("sys:mail:all")
    public Result update(SysMailTemplateEntity dto) {
        //校验类型
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);

        sysMailTemplateService.updateById(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:mail:all")
    public Result delete(@RequestBody Long[] ids) {

        sysMailTemplateService.deleteByIds(Arrays.asList(ids));

        return new Result();
    }

    @PostMapping("/send")
    @ApiOperation("发送邮件")
    @RequiresPermissions("sys:mail:all")
    public Result send(Long id, String mailTo, String mailCc, String params) throws Exception {
        boolean flag = sysMailTemplateService.sendMail(id, mailTo, mailCc, params);
        if (flag) {
            return new Result();
        }

        return error(500, "邮件发送失败");
    }

}