package com.style.common.loader;

import com.style.utils.file.FileUtils;
import com.style.utils.lang.ArrayUtils;
import com.style.utils.lang.ObjectUtils;
import com.style.utils.lang.SetUtils;
import com.style.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Properties工具类， 可载入多个properties、yml文件，
 * 相同的属性在最后载入的文件中的值将会覆盖之前的值，
 * 取不到从System.getProperty()获取。
 */
@SuppressWarnings("all")
public class PropertyUtils {

    private static final Logger logger = PropertyUtils.initLogger();

    // 正则表达式预编译：${}
    private static final Pattern pattern = Pattern.compile("\\$\\{.*?\\}");

    // 全局配置属性
    private final Properties properties = new Properties();

    // 默认配置文件
    private static final String[] defaultConfigFiles = new String[]{
            "classpath:application.yml",
            "classpath:config/application.yml",
            "classpath:configs/application.yml"};

    /**
     * -> 系统配置文件（classpath*:configs/sys-*.*）
     * -> 默认配置文件（defaultConfigFiles）
     * -> 外部配置文件（spring.config.location）
     * -> 活动配置文件（spring.profiles.active），configs/sys-*-default.*不会覆盖系统配置文件
     */
    private final Set<String> configSet = SetUtils.newLinkedHashSet();

    /**
     * 载入多个文件，路径使用Spring Resource格式，相同的属性在最后载入的文件中的值将会覆盖之前的值。
     */
    private PropertyUtils(Set<String> configFiles) {
        for (String location : configFiles) {
            try {
                Resource resource = ResourceUtils.getResource(location);
                if (resource.exists()) {
                    String ext = FileUtils.getFileExtension(location);
                    if ("properties".equals(ext)) {
                        try (InputStreamReader is = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
                            this.properties.load(is);
                            this.configSet.add(location);
                        } catch (IOException ex) {
                            logger.error("Load " + location + " failure. ", ex);
                        }
                    } else if ("yml".equals(ext)) {
                        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
                        bean.setResources(resource);
                        for (Map.Entry<Object, Object> entry : bean.getObject().entrySet()) {
                            this.properties.put(ObjectUtils.toString(entry.getKey()), ObjectUtils.toString(entry.getValue()));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Load " + location + " failure. ", e);
            }
        }
    }

    /**
     * 获取属性值，取不到从System.getProperty()获取，都取不到返回null
     *
     * @param key key
     * @return value
     */
    public String getProperty(String key) {
        String value = this.properties.getProperty(key);
        if (value != null) {
            // 支持嵌套取值的问题  key=${xx}/yy
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String g = matcher.group();
                String keyChild = g.replaceAll("\\$\\{", "").replaceAll("\\}", "");
                value = value.replace(g, getProperty(keyChild));
            }
            return value;
        } else {
            String systemProperty = System.getProperty(key);
            if (systemProperty != null) {
                return systemProperty;
            }
        }
        return null;
    }

    /**
     * 取出String类型的Property，但以System的Property优先，如果都为null则返回defaultValue值
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取当前加载的属性
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * 获取当前加载的属性文件
     */
    public Set<String> getConfigSet() {
        return this.configSet;
    }

    /**
     * 获取当前类实例
     *
     * @return instance
     */
    public static PropertyUtils getInstance() {
        return PropertiesLoaderHolder.INSTANCE;
    }

    /**
     * 重新加载实例（重新实例化，以重新加载属性文件数据）
     */
    public static void reloadInstance() {
        PropertiesLoaderHolder.reloadInstance();
    }

    /**
     * 初始化日志路径
     */
    private static Logger initLogger() {
        String logPath;
        try {
            // 获取当前classes目录
            logPath = new DefaultResourceLoader().getResource("/").getFile().getPath();
        } catch (Exception e) {
            // 取不到，取当前工作路径
            logPath = System.getProperty("user.dir");
        }
        // 取当前日志路径下有classes目录，则使用classes目录
        String classesLogPath = FileUtils.path(logPath + "/WEB-INF/classes");
        if (new File(classesLogPath).exists()) {
            logPath = classesLogPath;
        }
        System.setProperty("logPath", FileUtils.path(logPath));
        return LoggerFactory.getLogger(PropertyUtils.class);
    }

    /**
     * 当前类的实例持有者（静态内部类，延迟加载，懒汉式，线程安全的单例模式）
     */
    private static final class PropertiesLoaderHolder {

        private static PropertyUtils INSTANCE;

        static {
            PropertiesLoaderHolder.reloadInstance();
        }

        /**
         * 重新加载资源文件
         */
        private static void reloadInstance() {
            Set<String> configSet = SetUtils.newLinkedHashSet();
            // 搜索系统配置文件
            Resource[] resources = ResourceUtils.getResources("classpath*:configs/sys-*.*");
            for (Resource resource : resources) {
                configSet.add("classpath:configs/" + resource.getFilename());
            }

            // 搜索默认配置文件
            Set<String> set = SetUtils.newLinkedHashSet();
            set.addAll(ArrayUtils.toList(defaultConfigFiles));

            // 搜索外部配置文件
            String customConfigs = System.getProperty("spring.config.location");
            if (StringUtils.isNotBlank(customConfigs)) {
                for (String customConfig : StringUtils.split(customConfigs, ",")) {
                    if (!customConfig.contains("$")) {
                        customConfig = org.springframework.util.StringUtils.cleanPath(customConfig);
                        if (!ResourceUtils.isUrl(customConfig)) {
                            customConfig = ResourceUtils.FILE_URL_PREFIX + customConfig;
                        }
                    }
                    set.add(customConfig);
                }
            }

            // 搜索活动配置文件
            String profiles = System.getProperty("spring.profiles.active");
            if (StringUtils.isBlank(profiles)) {
                PropertyUtils propsTemp = new PropertyUtils(set);
                profiles = propsTemp.getProperty("spring.profiles.active");
            }
            for (String location : set) {
                configSet.add(location);
                if (StringUtils.isNotBlank(profiles)) {
                    if (location.endsWith(".properties")) {
                        configSet.add(StringUtils.substringBeforeLast(location, ".properties")
                                + "-" + profiles + ".properties");
                    } else if (location.endsWith(".yml")) {
                        configSet.add(StringUtils.substringBeforeLast(location, ".yml")
                                + "-" + profiles + ".yml");
                    }
                }
            }
            // 开始加载配置文件
            logger.debug("Loading config files: {}", configSet);
            PropertiesLoaderHolder.INSTANCE = new PropertyUtils(configSet);
        }
    }
}
