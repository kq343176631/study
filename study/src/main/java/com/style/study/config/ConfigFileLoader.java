package com.style.study.config;

import com.style.utils.lang.StringUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;

import java.util.List;

public class ConfigFileLoader {

    public static FileBasedConfigurationBuilder<XMLConfiguration> loadXmlConfig(String xmlFilePath) {

        if (StringUtils.isBlank(xmlFilePath)) {
            return null;
        }
        Parameters params = new Parameters();


        return new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(params.xml()
                        .setValidating(false)
                        .setFileName("xml-file.xml")
                        .setEntityResolver(new DefaultEntityResolver())
                        .setThrowExceptionOnMissing(false)
                        .setSchemaValidation(false)
                        .setExpressionEngine(new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS))
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    }

    public static void main(String[] args) throws ConfigurationException {

        FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder = ConfigFileLoader.loadXmlConfig("xml-file.xml");

        if (xmlBuilder == null) {
            return;
        }
        XMLConfiguration xmlConfig = xmlBuilder.getConfiguration();
        if (xmlConfig == null) {
            return;
        }
        xmlConfig.setProperty("token.validate.expire(0)[@name]",123);
        xmlBuilder.save();


    }

    /**
     * @param xmlConfig
     * @param key
     * @param obj
     * @param isMore    是否修改多个节点
     */
    public static void updateNodeValue(XMLConfiguration xmlConfig, String key, Object obj, boolean isMore) {
        List<HierarchicalConfiguration<ImmutableNode>> subConfigs = xmlConfig.configurationsAt(key);
        int size = subConfigs.size();
        if (size <= 1 || !isMore) {
            xmlConfig.setProperty(key + "(0)", obj);
            return;
        }
        for (int i = 0; i < size; i++) {
            xmlConfig.setProperty(key + "(" + i + ")", obj);
        }

    }

}
