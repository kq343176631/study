package com.style.common.utils;

import com.style.common.loader.ResourceUtils;
import com.style.utils.file.FileUtils;
import com.style.utils.lang.StringUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProjectUtils {

    private static Logger logger = LoggerFactory.getLogger(ProjectUtils.class);

    /**
     * 读取文件到字符串对象
     *
     * @param classResourcePath 资源文件路径加文件名
     * @return 文件内容
     * @author ThinkGem 2016-7-4
     */
    public static String readFileToString(String classResourcePath) {
        try (InputStream in = new ClassPathResource(classResourcePath).getInputStream()) {
            return IOUtils.toString(in, Charsets.toCharset("UTF-8"));
        } catch (IOException e) {
            logger.warn("Error file convert: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取工程源文件所在路径
     */
    @SuppressWarnings("all")
    public static String getProjectPath() {
        String projectPath = "";
        try {
            File file = ResourceUtils.getResource("").getFile();
            if (file != null) {
                while (true) {
                    File f = new File(FileUtils.path(file.getPath() + "/src/main"));
                    if (f.exists()) {
                        break;
                    }
                    f = new File(FileUtils.path(file.getPath() + "/target/classes"));
                    if (f.exists()) {
                        break;
                    }
                    if (file.getParentFile() != null) {
                        file = file.getParentFile();
                    } else {
                        break;
                    }
                }
                projectPath = file.toString();
            }
        } catch (IOException e) {
            // 忽略异常
        }
        // 取不到，取当前工作路径
        if (StringUtils.isBlank(projectPath)) {
            projectPath = System.getProperty("user.dir");
        }
        return projectPath;
    }

    /**
     * 获取工程源文件所在路径
     *
     * @return webAppSrcPath
     */
    @SuppressWarnings("all")
    public static String getWebAppPath() {
        String webAppPath = "";
        try {
            File file = ResourceUtils.getResource("").getFile();
            if (file != null) {
                while (true) {
                    File f = new File(FileUtils.path(file.getPath() + "/WEB-INF/classes"));
                    if (f.exists()) {
                        break;
                    }
                    f = new File(FileUtils.path(file.getPath() + "/src/main/webapp"));
                    if (f.exists()) {
                        return f.getPath();
                    }
                    if (file.getParentFile() != null) {
                        file = file.getParentFile();
                    } else {
                        break;
                    }
                }
                webAppPath = file.toString();
            }
        } catch (IOException e) {
            // 忽略异常
        }
        // 取不到，取当前工作路径
        if (StringUtils.isBlank(webAppPath)) {
            webAppPath = System.getProperty("user.dir");
        }
        return webAppPath;
    }
}
