package com.style.admin.modules.job.web;

import com.style.admin.modules.job.entity.ScheduleJob;
import com.style.admin.modules.job.service.ScheduleJobService;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
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
 * 定时任务
 */
@RestController
@RequestMapping("/sys/schedule")
@Api(tags = "定时任务")
public class ScheduleJobController extends WebController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beanName", value = "beanName", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:schedule:page")
    public Result<Page<ScheduleJob>> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(scheduleJobService.page(params));
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:schedule:get")
    public Result<ScheduleJob> get(@PathVariable("id") Long id) {

        return success(scheduleJobService.get(id));
    }

    @PostMapping
    @ApiOperation("保存")
    @RequiresPermissions("sys:schedule:save")
    public Result save(@RequestBody ScheduleJob dto) {

        ValidatorUtils.validateEntity(dto, AddGroup.class);
        if (scheduleJobService.save(dto)) {
            return success();
        }
        return error(ErrorCode.INSERT_RECORD_ERROR);
    }

    @PutMapping
    @ApiOperation("修改")
    @RequiresPermissions("sys:schedule:update")
    public Result update(@RequestBody ScheduleJob dto) {

        ValidatorUtils.validateEntity(dto, UpdateGroup.class);

        if (scheduleJobService.updateById(dto)) {
            return success();
        }

        return error(ErrorCode.UPDATE_RECORD_ERROR);
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:schedule:delete")
    public Result delete(@RequestBody Long[] ids) {

        if(scheduleJobService.deleteByIds(Arrays.asList(ids))){
            return success();
        }

        return error(ErrorCode.DELETE_RECORD_ERROR);
    }

    @PutMapping("/run")
    @ApiOperation("执行")
    @RequiresPermissions("sys:schedule:run")
    public Result run(@RequestBody Long[] ids) {

        scheduleJobService.run(ids);

        return success();
    }

    @PutMapping("/pause")
    @ApiOperation("暂停")
    @RequiresPermissions("sys:schedule:pause")
    public Result pause(@RequestBody Long[] ids) {

        scheduleJobService.pause(ids);

        return success();
    }

    @PutMapping("/resume")
    @ApiOperation("恢复")
    @RequiresPermissions("sys:schedule:resume")
    public Result resume(@RequestBody Long[] ids) {

        scheduleJobService.resume(ids);

        return success();
    }

}