package com.style.admin.modules.demo.web;

import com.style.admin.modules.demo.entity.News;
import com.style.admin.modules.demo.service.NewsService;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
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
import java.util.Map;

@RestController
@RequestMapping("demo/news")
@Api(tags = "新闻管理")
public class NewsController extends WebController {

    @Autowired
    private NewsService newsService;

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("demo:news:list")
    public Result get(@PathVariable("id") Long id) {

        return success(newsService.get(id));
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("demo:news:list")
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(newsService.page(paramsToLike(params, "title")));
    }

    @PostMapping
    @ApiOperation("保存")
    @RequiresPermissions("demo:news:edit")
    public Result save(News news) {

        //效验数据
        ValidatorUtils.validateEntity(news, AddGroup.class);

        if (newsService.save(news)) {
            return success();
        }
        return error(ErrorCode.INSERT_RECORD_ERROR);
    }

    @PutMapping
    @ApiOperation("修改")
    @RequiresPermissions("demo:news:edit")
    public Result update(News news) {

        //效验数据
        ValidatorUtils.validateEntity(news, UpdateGroup.class);

        if (newsService.updateById(news)) {
            return success();
        }
        return error(ErrorCode.UPDATE_RECORD_ERROR);
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("demo:news:edit")
    public Result delete(@RequestBody Long[] ids) {

        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        if (newsService.deleteByIds(Arrays.asList(ids))) {
            return success();
        }
        return error(ErrorCode.DELETE_RECORD_ERROR);

    }

}
