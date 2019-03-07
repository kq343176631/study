package com.style.admin.modules.activiti.service;

import com.style.common.constant.Constants;
import com.style.common.model.Page;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例
 */
@Service
public class ActRunningService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 流程实例列表
     */
    public Page<Map<String, Object>> page(Map<String, Object> params) {
        String id = (String) params.get("id");
        String definitionKey = (String) params.get("definitionKey");

        //分页参数
        Integer curPage = 1;
        Integer limit = 10;
        if (params.get(Constants.PAGE_NO) != null) {
            curPage = Integer.parseInt((String) params.get(Constants.PAGE_NO));
        }
        if (params.get(Constants.PAGE_SIZE) != null) {
            limit = Integer.parseInt((String) params.get(Constants.PAGE_SIZE));
        }

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotBlank(id)) {
            processInstanceQuery.processInstanceId(id);
        }
        if (StringUtils.isNotBlank(definitionKey)) {
            processInstanceQuery.processDefinitionKey(definitionKey);
        }

        List<ProcessInstance> processInstanceList = processInstanceQuery.listPage((curPage - 1) * limit, limit);
        List<Map<String, Object>> objectList = new ArrayList<>();
        for (ProcessInstance processInstance : processInstanceList) {
            objectList.add(processInstanceConvert(processInstance));
        }
        Page<Map<String, Object>> page = new Page<>();
        page.setCurrent(curPage);
        page.setSize(limit);
        page.setTotal((int) processInstanceQuery.count());
        page.setRecords(objectList);
        return page;
    }

    /**
     * 流程实例信息
     */
    private Map<String, Object> processInstanceConvert(ProcessInstance processInstance) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("id", processInstance.getId());
        map.put("processInstanceId", processInstance.getProcessInstanceId());
        map.put("processDefinitionId", processInstance.getProcessDefinitionId());
        map.put("processDefinitionName", processInstance.getProcessDefinitionName());
        map.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        map.put("activityId", processInstance.getActivityId());
        map.put("suspended", processInstance.isSuspended());

        return map;
    }

    /**
     * 删除实例
     *
     * @param id 实例ID
     */
    public void delete(String id) {
        runtimeService.deleteProcessInstance(id, null);
    }

    /**
     * 启动流程实例
     *
     * @param key 流程定义标识key
     */
    public void startProcess(String key) {
        runtimeService.startProcessInstanceByKey(key);
    }
}
