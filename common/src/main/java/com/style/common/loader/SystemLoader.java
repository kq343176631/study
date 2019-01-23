package com.style.common.loader;

import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 加载系统配置文件
 */
public class SystemLoader implements org.springframework.boot.env.PropertySourceLoader {

    // 记录资源是否加载过，防止重复加载。
    private static boolean isLoadPropertySource = false;

    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties", "yml"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) {

        if (!isLoadPropertySource) {
            isLoadPropertySource = true;
            Properties properties = PropertyUtils.getInstance().getProperties();
            return Collections.singletonList(new OriginTrackedMapPropertySource("style", properties));
        }

        return Collections.emptyList();
    }

}
