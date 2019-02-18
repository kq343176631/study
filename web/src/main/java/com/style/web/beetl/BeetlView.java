package com.style.web.beetl;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.ext.web.WebRender;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * 自定义 Beetl View
 */
public class BeetlView extends AbstractTemplateView {

    protected GroupTemplate groupTemplate = null;

    public void setGroupTemplate(GroupTemplate groupTemplate) {
        this.groupTemplate = groupTemplate;
    }

    public GroupTemplate getGroupTemplate() {
        return this.groupTemplate;
    }

    public BeetlView() {
    }

    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws NoSuchBeanDefinitionException, NoUniqueBeanDefinitionException {
        if (this.groupTemplate == null) {
            this.groupTemplate = (GroupTemplate) this.getApplicationContext().getBean(GroupTemplate.class);
        }

        WebRender render = new WebRender(this.groupTemplate) {
            protected void modifyTemplate(Template template, String key, HttpServletRequest request, HttpServletResponse response, Object... args) {
                Map<?, ?> model = (Map) args[0];
                Iterator var7 = model.entrySet().iterator();

                while (var7.hasNext()) {
                    Map.Entry<?, ?> entry = (Map.Entry) var7.next();
                    String name = (String) entry.getKey();
                    Object value = entry.getValue();
                    template.binding(name, value);
                }

            }
        };
        String path = this.getUrl();
        render.render(path, request, response, new Object[]{model});
    }

    public boolean checkResource(Locale locale) throws Exception {
        String url = this.getUrl();
        if (url.contains("#")) {
            String[] split = url.split("#");
            if (split.length > 2) {
                throw new Exception("视图名称有误：" + url);
            } else {
                return this.groupTemplate.getResourceLoader().exist(split[0]);
            }
        } else {
            return this.groupTemplate.getResourceLoader().exist(url);
        }
    }

}
