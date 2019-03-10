package com.style.admin.modules.sys.web;

import com.style.admin.modules.sys.entity.System;
import com.style.common.model.Result;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 系统接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@Api(tags = "系统接口")
public class SystemController {

    @GetMapping("sys/info")
    @ApiOperation("系统信息")
    public Result<System> info() {

        OperatingSystemMXBean osmx = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        System dto = new System();
        dto.setSysTime(java.lang.System.currentTimeMillis());
        dto.setOsName(java.lang.System.getProperty("os.name"));
        dto.setOsArch(java.lang.System.getProperty("os.arch"));
        dto.setOsVersion(java.lang.System.getProperty("os.version"));
        dto.setUserLanguage(java.lang.System.getProperty("user.language"));
        dto.setUserDir(java.lang.System.getProperty("user.dir"));
        dto.setTotalPhysical(osmx.getTotalPhysicalMemorySize() / 1024 / 1024);
        dto.setFreePhysical(osmx.getFreePhysicalMemorySize() / 1024 / 1024);
        dto.setMemoryRate(BigDecimal.valueOf((1 - osmx.getFreePhysicalMemorySize() * 1.0 / osmx.getTotalPhysicalMemorySize()) * 100).setScale(2, RoundingMode.HALF_UP));
        dto.setProcessors(osmx.getAvailableProcessors());
        dto.setJvmName(java.lang.System.getProperty("java.vm.name"));
        dto.setJavaVersion(java.lang.System.getProperty("java.version"));
        dto.setJavaHome(java.lang.System.getProperty("java.home"));
        dto.setJavaTotalMemory(Runtime.getRuntime().totalMemory() / 1024 / 1024);
        dto.setJavaFreeMemory(Runtime.getRuntime().freeMemory() / 1024 / 1024);
        dto.setJavaMaxMemory(Runtime.getRuntime().maxMemory() / 1024 / 1024);
        dto.setUserName(java.lang.System.getProperty("user.name"));
        dto.setSystemCpuLoad(BigDecimal.valueOf(osmx.getSystemCpuLoad() * 100).setScale(2, RoundingMode.HALF_UP));
        dto.setUserTimezone(java.lang.System.getProperty("user.timezone"));

        return new Result<>(dto);
    }

}