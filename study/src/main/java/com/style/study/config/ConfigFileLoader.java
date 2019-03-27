package com.style.study.config;

import com.style.utils.io.FileUtils;
import com.style.utils.lang.StringUtils;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.NodeModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigFileLoader {

    public static XMLConfiguration loadXmlConfig(String xmlFilePath) {

        if (StringUtils.isBlank(xmlFilePath)) {
            return null;
        }

        Configurations configurations = new Configurations();
        try {

            XMLConfiguration xmlConfiguration = configurations.xml(xmlFilePath);
            xmlConfiguration.addProperty("filePath",xmlFilePath);
            //xmlConfiguration.setRootElementName();
            return xmlConfiguration;

        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {

        XMLConfiguration xmlConfig = ConfigFileLoader.loadXmlConfig("xml-file.xml");
        if(xmlConfig ==null){
            return;
        }

        System.out.println("RootName:"+xmlConfig.getRootElementName());
        System.out.println("token.person.expire:"+xmlConfig.getString("token.person.expire"));
        //System.out.println("token.person.expire:"+xmlConfig.getProperty("token.person.expire"));

        String outPath = FileUtils.getProjectPath()+ "/src/main/resources/xml"+StringUtils.getRandomStr(5)+".xml";

        xmlConfig.clearTree("filePath");

        NodeModel nodeModel= xmlConfig.getNodeModel();
        //nodeModel.
        /*File outputFile = new File(outPath);

        try {
            xmlConfig.write(new FileWriter(outputFile));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

}
