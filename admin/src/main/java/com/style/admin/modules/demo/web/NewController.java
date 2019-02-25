package com.style.admin.modules.demo.web;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.style.admin.modules.demo.entity.News;
import com.style.admin.modules.demo.service.NewsService;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.AssertUtils;
import com.style.common.validator.ValidatorUtils;
import com.style.common.validator.group.AddGroup;
import com.style.common.validator.group.DefaultGroup;
import com.style.common.validator.group.UpdateGroup;
import com.style.common.web.WebController;
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

@RestController
public class NewController extends WebController {

    @Autowired
    private NewsService newsService;

    @ApiOperation("信息")
    @GetMapping("{id}")
    @RequiresPermissions("demo:news:all")
    public Result<News> get(@PathVariable("id") String id) {

        return new Result<>(newsService.get(id));
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", paramType = "query", dataType = "String")
    })
    public Result<List<News>> list(Map<String ,Object> params){

        return new Result<>(newsService.list(params));
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("demo:news:all")
    public Result<Page> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return new Result<>(newsService.page(paramsToLike(params, "title")));
    }

    @PostMapping
    @ApiOperation("保存")
    @RequiresPermissions("demo:news:all")
    public Result save(News news) {

        //效验数据
        ValidatorUtils.validateEntity(news, AddGroup.class, DefaultGroup.class);

        return new Result<>(newsService.save(news));
    }

    @PutMapping
    @ApiOperation("修改")
    @RequiresPermissions("demo:news:all")
    public Result update(News news) {

        //效验数据
        ValidatorUtils.validateEntity(news, UpdateGroup.class, DefaultGroup.class);

        newsService.update(news, new UpdateWrapper<>());

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("demo:news:all")
    public Result delete(@RequestBody String[] ids) {

        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        newsService.deleteByIds(Arrays.asList(ids));

        return new Result();

    }

}
