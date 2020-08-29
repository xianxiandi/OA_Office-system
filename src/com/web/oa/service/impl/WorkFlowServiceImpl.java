package com.web.oa.service.impl;

import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    @Autowired
    private BaoxiaoBillMapper baoxiaoBillMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;


    /**部署流程定义*/
    @Override
    public void saveNewDeploy(InputStream inputStream, String processName) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                 repositoryService.createDeployment()
                    .name(processName)
                    .addZipInputStream(zipInputStream)
                    .deploy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = repositoryService.createDeploymentQuery()
                .orderByDeploymenTime().asc()
                .list();
        return list;
    }

    @Override
    public List<ProcessDefinition> findProcessDefinitionList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()
                .list();
        return list;
    }

    @Override
    public void saveStartProcess(Long id, String username) {
        String key= Constants.BAOXIAO_KEY;
        Map<String,Object> variables=new HashMap<String, Object>();
        variables.put("inputUser",username);
        String objId=key+"."+id;
        variables.put("objId",objId);
        runtimeService.startProcessInstanceByKey(key,objId,variables);
    }

    @Override
    public List<Task> findTaskListByName(String username) {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(username)
                .orderByTaskCreateTime().asc()
                .list();
        return list;
    }

    @Override
    public BaoxiaoBill findBaoxiaoBillByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId)
                .singleResult();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        String bussiness_key=pi.getBusinessKey();
        String id="";
        if (StringUtils.isNotBlank(bussiness_key)){
            id=bussiness_key.split("\\.")[1];
        }
        BaoxiaoBill bill = baoxiaoBillMapper.selectByPrimaryKey(Long.parseLong(id));
        return bill;
    }

    @Override
    public List<Comment> findCommentByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();
        List<Comment> processInstanceComments = taskService.getProcessInstanceComments(processInstanceId);
        return processInstanceComments;
    }

    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        //返回存放连线的名称集合
        List<String> list=new ArrayList<String>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String activityId = processInstance.getActivityId();
        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
        List<PvmTransition> pvList = activity.getOutgoingTransitions();
        if (pvList!=null&&pvList.size()>0){
            for (PvmTransition pvmTransition : pvList) {
               String name= (String) pvmTransition.getProperty("name");
               if (StringUtils.isNotBlank(name)){
                   list.add(name);
               }else {
                   list.add("默认提交");
               }
            }
        }
        return list;
    }

    @Override
    public void saveSubmitTask(Long id, String taskId, String comment, String outcome, String username) {
        /**
         * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        //加当前任务的审核人
        Authentication.setAuthenticatedUserId(username);
        //添加批注
        taskService.addComment(taskId,processInstanceId,comment);
        /**
         * 2：如果连线的名称是“默认提交”，那么就不需要设置，如果不是，就需要设置流程变量
         * 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
         流程变量的名称：outcome
         流程变量的值：连线的名称
         */
        Map<String,Object> variables=new HashMap<String, Object>();
        if (outcome!=null&&!outcome.equals("默认提交")){
            variables.put("message",outcome);
            taskService.complete(taskId,variables);
        }else {
            taskService.complete(taskId);
        }
        /**
         * 5：在完成任务之后，判断流程是否结束
         如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
         */
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance==null){
            BaoxiaoBill bill = baoxiaoBillMapper.selectByPrimaryKey(id);
            bill.setState(2);
            baoxiaoBillMapper.updateByPrimaryKey(bill);
        }
    }

    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        //使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        return pd;
    }

    @Override
    public Map<String, Object> findCoordingByTaskId(String taskId) {
        //存放坐标
        Map<String, Object> map = new HashMap<String,Object>();
        //使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String activityId = pi.getActivityId();
        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
        map.put("x",activity.getX());
        map.put("y",activity.getY());
        map.put("width",activity.getWidth());
        map.put("height",activity.getHeight());
        return map;
    }

    /**使用部署对象ID和资源图片名称，获取图片的输入流*/
    @Override
    public InputStream findImageIputStream(String deploymentId, String imageName) {

        return repositoryService.getResourceAsStream(deploymentId,imageName);
    }

    @Override
    public List<Comment> findCommentByBaoxiaoBillId(Long id) {
       String bussiness_key= Constants.BAOXIAO_KEY+"."+id;
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(bussiness_key)
                .singleResult();
        List<Comment> commentList = taskService.getProcessInstanceComments(pi.getId());

        return commentList;
    }

    @Override
    public Task findTaskByBussinessKey(String bussiness_key) {
        return taskService.createTaskQuery().processInstanceBusinessKey(bussiness_key).singleResult();
    }

    @Override
    public void deleteDeploymentIdByDeploymentId(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId,true);
    }

}
