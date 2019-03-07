package com.style.admin.modules.activiti.web;

import com.style.admin.modules.activiti.entity.ActModel;
import com.style.admin.modules.activiti.service.ActModelService;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.validator.ValidatorUtils;
import com.style.common.web.WebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 模型管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/act/model")
@Api(tags = "模型管理")
public class ActModelController extends WebController {

    @Autowired
    private ActModelService actModelService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "name", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:model:all")
    public Result<Page<Model>> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(actModelService.page(params));
    }

    @PostMapping
    @ApiOperation("新增模型")
    @RequiresPermissions("sys:model:all")
    public Result save(@RequestBody ActModel actModel) throws Exception {

        //效验数据
        ValidatorUtils.validateEntity(actModel);

        actModelService.save(actModel.getName(), actModel.getKey(), actModel.getDescription());

        return new Result();
    }

    @PostMapping("deploy/{id}")
    @ApiOperation("部署")
    @RequiresPermissions("sys:model:all")
    public Result deploy(@PathVariable("id") String id) {

        actModelService.deploy(id);

        return new Result();
    }

    @GetMapping("export/{id}")
    @ApiOperation("导出")
    @RequiresPermissions("sys:model:all")
    public void export(@PathVariable("id") String id, @ApiIgnore HttpServletResponse response) {

        actModelService.export(id, response);
    }

    @DeleteMapping
    @ApiOperation("删除")
    @RequiresPermissions("sys:model:all")
    public Result delete(@RequestBody String[] ids) {
        for (String id : ids) {
            actModelService.delete(id);
        }
        return new Result();
    }
}