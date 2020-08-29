package com.web.oa.service;


import com.web.oa.pojo.BaoxiaoBill;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {
    void saveNewDeploy(InputStream inputStream, String processName);

    List<Deployment> findDeploymentList();

    List<ProcessDefinition> findProcessDefinitionList();

    void saveStartProcess(Long id, String username);

    List<Task> findTaskListByName(String username);

    BaoxiaoBill findBaoxiaoBillByTaskId(String taskId);

    List<Comment> findCommentByTaskId(String taskId);

    List<String> findOutComeListByTaskId(String taskId);

    void saveSubmitTask(Long id, String taskId, String comment, String outcome, String username);

    ProcessDefinition findProcessDefinitionByTaskId(String taskId);

    Map<String,Object> findCoordingByTaskId(String taskId);

    InputStream findImageIputStream(String deploymentId, String imageName);

    List<Comment> findCommentByBaoxiaoBillId(Long id);

    Task findTaskByBussinessKey(String bussiness_key);

    void deleteDeploymentIdByDeploymentId(String deploymentId);
}
