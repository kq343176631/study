package com.style.admin.modules.msg.web;

import com.style.admin.modules.msg.service.SysMailLogService;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.model.Result;
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
 * 邮件发送记录
 */
@RestController
@RequestMapping("sys/maillog")
@Api(tags = "邮件发送记录")
public class MailLogController extends WebController {

    @Autowired
    private SysMailLogService sysMailLogService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "templateId", value = "templateId", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mailTo", value = "mailTo", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "status", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:mail:log")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return success(sysMailLogService.page(params));
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:mail:log")
    public Result delete(@RequestBody Long[] ids) {
        sysMailLogService.deleteByIds(Arrays.asList(ids));

        return new Result();
    }

}