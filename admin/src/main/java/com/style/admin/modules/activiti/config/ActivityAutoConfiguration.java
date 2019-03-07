package com.style.admin.modules.activiti.config;

import org.activiti.engine.*;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 手动配置
 */
//@Configuration
public class ActivityAutoConfiguration {

    @Bean
    public ProcessEngine processEngine(PlatformTransactionManager transactionManager, DataSource dataSource) throws IOException {

        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();

        //自动部署已有的流程文件
        //Resource[] resources = ResourceUtils.getResources("classpath:processes/*.bpmn");

        configuration.setTransactionManager(transactionManager);
        configuration.setDataSource(dataSource);
        configuration.setDatabaseSchemaUpdate("true");
        //configuration.setDeploymentResources(resources);
        configuration.setDbIdentityUsed(false);

        return configuration.buildProcessEngine();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

}
