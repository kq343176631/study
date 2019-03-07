package com.style.admin.modules.activiti.web;

import com.style.admin.modules.activiti.service.ActProcessService;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
import com.style.common.model.Page;
import com.style.common.model.Result;
import com.style.common.web.WebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 流程管理
 */
@RestController
@RequestMapping("/act/process")
@Api(tags = "流程管理")
public class ActProcessController extends WebController {

    @Autowired
    private ActProcessService actProcessService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constants.PAGE_NO, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constants.PAGE_SIZE, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "processName", value = "processName", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:process:all")
    public Result<Page<Map<String, Object>>> page(@ApiIgnore @RequestParam Map<String, Object> params) {

        return success(actProcessService.page(params));
    }

    @PostMapping("deploy")
    @ApiOperation("部署流程文件")
    @ApiImplicitParam(name = "processFile", value = "流程文件", paramType = "query", dataType = "file")
    @RequiresPermissions("sys:process:all")
    public Result deploy(@RequestParam("processFile") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return error(ErrorCode.UPLOAD_FILE_EMPTY);
        }

        actProcessService.deploy(file);

        return new Result();
    }

    @PutMapping("active/{id}")
    @ApiImplicitParam(name = "id", value = "流程ID", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:process:all")
    public Result active(@PathVariable("id") String id) {

        actProcessService.active(id);

        return new Result();
    }

    @PutMapping("suspend/{id}")
    @ApiOperation("挂起流程")
    @ApiImplicitParam(name = "id", value = "流程ID", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:process:all")
    public Result suspend(@PathVariable("id") String id) {

        actProcessService.suspend(id);

        return new Result();
    }

    @PostMapping("convertToModel/{id}")
    @ApiOperation("将部署的流程转换为模型")
    @ApiImplicitParam(name = "id", value = "流程ID", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:process:all")
    public Result convertToModel(@PathVariable("id") String id) throws Exception {

        actProcessService.convertToModel(id);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除流程")
    @RequiresPermissions("sys:process:all")
    public Result delete(@RequestBody String[] deploymentIds) {
        for (String deploymentId : deploymentIds) {
            actProcessService.deleteDeployment(deploymentId);
        }
        return new Result();
    }

    @GetMapping(value = "resource")
    @ApiOperation("获取资源文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deploymentId", value = "部署ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "resourceName", value = "资源名称", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:process:all")
    public void resource(String deploymentId, String resourceName, @ApiIgnore HttpServletResponse response) throws Exception {
        InputStream resourceAsStream = actProcessService.getResourceAsStream(deploymentId, resourceName);

        IOUtils.copy(resourceAsStream, response.getOutputStream());
    }

}