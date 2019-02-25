package com.style.login.modules.demo.web;

import com.style.common.constant.Constants;
import com.style.common.web.WebController;
import com.style.common.model.Result;
import com.style.login.modules.demo.entity.News;
import com.style.login.modules.demo.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("demo/news")
@Api(tags="新闻管理")
public class NewsController extends WebController {

    @Autowired
    private NewsService newsService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constants.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constants.ORDER_METHOD, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "startDate", value = "开始时间", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", paramType = "query", dataType="String")
    })
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params){

        return new Result();
    }

    @ApiOperation("信息")
    @GetMapping("{id}")
    public Result info(@PathVariable("id") Long id){

        return new Result();
    }

    @PostMapping
    @ApiOperation("保存")
    public Result save(News dto){

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    public Result update(News dto){

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){

        return new Result();
    }

}
