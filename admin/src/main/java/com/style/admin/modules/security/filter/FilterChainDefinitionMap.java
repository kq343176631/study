package com.style.admin.modules.security.filter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;

public class FilterChainDefinitionMap implements FactoryBean<Section> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String filterChainDefinitions;

    private String defaultFilterChainDefinitions;

    private Section section;

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setDefaultFilterChainDefinitions(String defaultFilterChainDefinitions) {
        this.defaultFilterChainDefinitions = defaultFilterChainDefinitions;
    }

    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }

    public FilterChainDefinitionMap() {
    }

    @Override
    public Section getObject() throws BeansException {
        if (this.section == null) {
            Ini ini = new Ini();
            ini.load(this.defaultFilterChainDefinitions);
            this.section = ini.getSection("");
            ini.load(this.filterChainDefinitions);
            this.section.putAll(ini.getSection(""));
            this.logger.debug("Init Section", this.section.entrySet());
        }
        return this.section;
    }
}
