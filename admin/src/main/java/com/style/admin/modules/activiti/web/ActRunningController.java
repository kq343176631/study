package com.style.admin.modules.activiti.web;

import com.style.admin.modules.activiti.service.ActRunningService;
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

import java.util.Map;

/**
 * 运行中的流程
 */
@RestController
@RequestMapping("/act/running")
@Api(tags = "运行中的流程")
public class ActRunningController extends WebController {

    @Autowired
    private ActRunningService actRunningService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "id", value = "实例ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "definitionKey", value = "definitionKey", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:running:all")
    public Result<Page<Map<String, Object>>> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(actRunningService.page(params));
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除")
    @RequiresPermissions("sys:running:all")
    @ApiImplicitParam(name = "id", value = "ID", paramType = "query", dataType = "String")
    public Result deleteInstance(@PathVariable("id") String id) {

        actRunningService.delete(id);

        return new Result();
    }

    @PostMapping("start")
    @ApiOperation("启动流程实例")
    @ApiImplicitParam(name = "key", value = "流程定义标识key", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:running:all")
    public Result start(String key) {

        actRunningService.startProcess(key);

        return new Result();
    }

}