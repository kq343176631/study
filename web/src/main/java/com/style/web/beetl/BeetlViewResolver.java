package com.style.web.beetl;

import com.style.common.lang.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.util.Map;

/**
 * 自定义视图解析器
 */
public class BeetlViewResolver extends AbstractTemplateViewResolver implements InitializingBean, BeanNameAware {

    protected final Log logger = LogFactory.getLog(this.getClass());
    private String beanName = null;
    private BeetlGroupUtilConfiguration config;
    private GroupTemplate groupTemplate = null;

    public BeetlViewResolver() {
        this.setViewClass(BeetlView.class);
    }

    /**
     * 初始化视图解析器
     */
    public void afterPropertiesSet() throws NoSuchBeanDefinitionException, NoUniqueBeanDefinitionException, SecurityException, NoSuchFieldException {
        if (this.config == null) {
            this.config = this.getApplicationContext().getBean(BeetlGroupUtilConfiguration.class);
            this.groupTemplate = this.config.getGroupTemplate();
        }

        // 初始化共享变量
        Map<String, Object> sharedVars = this.groupTemplate.getSharedVars();
        if (sharedVars == null) {
            sharedVars = MapUtils.newHashMap();
        }
        String ctxPath = this.getServletContext().getContextPath();
        sharedVars.put("ctxPath", ctxPath);
        sharedVars.put("ctx", ctxPath);
        sharedVars.put("ctxStatic", new StringBuilder().insert(0, ctxPath).append("/Static"));
        sharedVars.put("_version", "2.0");
        this.groupTemplate.setSharedVars(sharedVars);

        if (this.getContentType() == null) {
            String charset = this.groupTemplate.getConf().getCharset();
            this.setContentType("text/html;charset=" + charset);
        }

    }

    protected Class<BeetlView> requiredViewClass() {
        return BeetlView.class;
    }

    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        BeetlView beetlView = (BeetlView) super.buildView(viewName);
        beetlView.setGroupTemplate(this.groupTemplate);
        String suffix = this.getSuffix();
        if (suffix != null && suffix.length() != 0 && viewName.contains("#")) {
            String[] split = viewName.split("#");
            if (split.length > 2) {
                throw new Exception("视图名称有误：" + viewName);
            }

            beetlView.setUrl(this.getPrefix() + split[0] + this.getSuffix() + "#" + split[1]);
        }

        return beetlView;
    }

    public static String redirect(String url) {
        return "redirect:" + url;
    }

    public static String forward(String url) {
        return "forward:" + url;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setGroupTemplate(GroupTemplate groupTemplate) {
        this.groupTemplate = groupTemplate;
    }

    public BeetlGroupUtilConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(BeetlGroupUtilConfiguration config) {
        this.config = config;
        this.groupTemplate = config.getGroupTemplate();
    }

    public void setPrefix(String prefix) {
        this.logger.warn("Beetl不建议使用使用spring前缀，会导致include,layout找不到对应的模板，请使用beetl的配置RESOURCE.ROOT来配置模板根目录");
        super.setPrefix(prefix);
    }

}
