package com.style.admin.modules.sys.web;

import com.style.admin.modules.sys.entity.SysDict;
import com.style.admin.modules.sys.service.SysDictService;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.AssertUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 数据字典
 */
@RestController
@RequestMapping("sys/dict")
@Api(tags = "数据字典")
public class SysDictController extends WebController {

    @Autowired
    private SysDictService sysDictService;

    @GetMapping("page")
    @ApiOperation("字典分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dictType", value = "字典类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dictName", value = "字典名称", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:dict:page")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        //字典分类
        return success(sysDictService.page(params));
    }

    @GetMapping("list")
    @ApiOperation("字典分类数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictName", value = "字典名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dictValue", value = "字典值", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:dict:page")
    public Result<List<SysDict>> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        //字典分类数据
        return success(sysDictService.list(params));
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:dict:info")
    public Result<SysDict> get(@PathVariable("id") Long id) {

        return success(sysDictService.get(id));
    }

    @PostMapping
    @ApiOperation("保存")
    @RequiresPermissions("sys:dict:save")
    public Result save(@RequestBody SysDict dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);

        sysDictService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @RequiresPermissions("sys:dict:update")
    public Result update(@RequestBody SysDict dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);

        sysDictService.updateById(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:dict:delete")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysDictService.deleteByIds(Arrays.asList(ids));

        return new Result();
    }

}