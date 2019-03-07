package com.style.admin.modules.activiti.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.style.common.constant.Constants;
import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.common.model.Page;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 模型管理
 */
@Service
public class ActModelService {

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    public Page<Model> page(Map<String, Object> params) {
        String key = (String) params.get("key");
        String name = (String) params.get("name");

        //分页参数
        Integer curPage = 1;
        Integer limit = 10;
        if (params.get(Constants.PAGE_NO) != null) {
            curPage = Integer.parseInt((String) params.get(Constants.PAGE_NO));
        }
        if (params.get(Constants.PAGE_SIZE) != null) {
            limit = Integer.parseInt((String) params.get(Constants.PAGE_SIZE));
        }

        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();

        if (StringUtils.isNotEmpty(key)) {
            modelQuery.modelKey(key);
        }
        if (StringUtils.isNotEmpty(name)) {
            modelQuery.modelName(name);
        }

        List<Model> list = modelQuery.listPage((curPage - 1) * limit, limit);

        Page<Model> page = new Page<>();
        page.setCurrent(curPage);
        page.setSize(limit);
        page.setRecords(list);
        page.setTotal((int) modelQuery.count());
        return page;
    }

    /**
     * 新建模型
     */
    public void save(String name, String key, String description) throws UnsupportedEncodingException {

        //新建一个空模型
        Model model = repositoryService.newModel();

        //metaInfo信息
        ObjectNode metaInfo = objectMapper.createObjectNode();
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, name);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, model.getVersion());

        model.setKey(key);
        model.setName(name);
        model.setMetaInfo(metaInfo.toString());

        repositoryService.saveModel(model);

        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilset = objectMapper.createObjectNode();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilset);
        repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
    }

    /**
     * 部署模型
     */
    public void deploy(String id) {
        try {
            Model model = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            String processName = model.getName();
            if (!StringUtils.endsWith(processName, ".bpmn20.xml")) {
                processName += ".bpmn20.xml";
            }

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            Deployment deployment = repositoryService.createDeployment().name(model.getName()).addInputStream(processName, in).deploy();

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            if (list.size() == 0) {
                throw new ValidateException(ErrorCode.ACT_DEPLOY_ERROR);
            }
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.ACT_MODEL_IMG_ERROR, e);
        }
    }

    /**
     * 导出模型
     */
    public void export(String id, HttpServletResponse response) {
        try {
            Model model = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.flushBuffer();
        } catch (Exception e) {
            throw new ValidateException(ErrorCode.ACT_MODEL_EXPORT_ERROR, id);
        }
    }

    /**
     * 删除模型
     */
    public void delete(String id) {
        repositoryService.deleteModel(id);
    }
}
