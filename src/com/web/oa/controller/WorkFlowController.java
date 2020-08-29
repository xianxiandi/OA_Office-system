package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoService;
import com.web.oa.service.WorkFlowService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private BaoxiaoService baoxiaoService;


    //部署流程
    @RequestMapping("/deployProcess")
    public String deployProcess(String processName, MultipartFile fileName){
        try {
            workFlowService.saveNewDeploy(fileName.getInputStream(),processName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/processDefinitionList";
    }

    @RequestMapping("/processDefinitionList")
    public ModelAndView processDefinitionList(){
        ModelAndView mv=new ModelAndView();
        //1:查询部署对象信息，对应表（act_re_deployment）
        List<Deployment> delist=workFlowService.findDeploymentList();
        //2:查询流程定义的信息，对应表（act_re_procdef）
        List<ProcessDefinition> pdList=workFlowService.findProcessDefinitionList();
        mv.addObject("depList",delist);
        mv.addObject("pdList",pdList);
        mv.setViewName("workflow_list");
        return mv;
    }
    @RequestMapping("/saveStartBaoxiao")
    public String saveStartBaoxiao(BaoxiaoBill baoxiaoBill, HttpSession session){
        baoxiaoBill.setCreatdate(new Date());

        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        baoxiaoBill.setUserId(activeUser.getId());
        baoxiaoBill.setState(1);
        baoxiaoService.saveBaoxiao(baoxiaoBill);
        workFlowService.saveStartProcess(baoxiaoBill.getId(),activeUser.getUsername());

        return "redirect:/myTaskList";
    }

    @RequestMapping("/myTaskList")
    public ModelAndView myTaskList(){
        ModelAndView mv=new ModelAndView();
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Task> list= workFlowService.findTaskListByName(activeUser.getUsername());
        mv.addObject("taskList",list);
        mv.setViewName("workflow_task");
        return mv;
    }
    @RequestMapping("/viewTaskForm")
    public ModelAndView viewTaskForm(String taskId){
        ModelAndView mv=new ModelAndView();
       BaoxiaoBill bill= workFlowService.findBaoxiaoBillByTaskId(taskId);

       List<Comment> list=workFlowService.findCommentByTaskId(taskId);

       List<String> outcomeList=workFlowService.findOutComeListByTaskId(taskId);
       mv.addObject("baoxiaoBill",bill);
       mv.addObject("commentList",list);
       mv.addObject("outcomeList",outcomeList);
       mv.addObject("taskId",taskId);
       mv.setViewName("approve_baoxiao");
        return  mv;
    }
    @RequestMapping("/submitTask")
    public String submitTask(Long id,String taskId,String comment,String outcome){
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        String username = activeUser.getUsername();
        workFlowService.saveSubmitTask(id,taskId,comment,outcome,username);
        return "redirect:/myTaskList";
    }
    @RequestMapping("/viewCurrentImage")
    public String viewCurrentImage(String taskId, ModelMap model){
        /**一：查看流程图*/
        ProcessDefinition pd= workFlowService.findProcessDefinitionByTaskId(taskId);
        model.addAttribute("deploymentId",pd.getDeploymentId());
        model.addAttribute("imageName",pd.getDiagramResourceName());
        Map<String,Object> map=workFlowService.findCoordingByTaskId(taskId);
        model.addAttribute("acs",map);

        return "viewimage";
    }
    @RequestMapping("/viewImage")
    public String viewimage(String deploymentId, String imageName, HttpServletResponse response) throws IOException {
        //2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
       InputStream in= workFlowService.findImageIputStream(deploymentId,imageName);
        OutputStream outputStream = response.getOutputStream();
        for (int b=-1;(b=in.read())!=-1;){
            outputStream.write(b);
        }
        outputStream.close();
        in.close();
        return null;
    }
    @RequestMapping("/delDeployment")
    public String delDeployment(String deploymentId){
        workFlowService.deleteDeploymentIdByDeploymentId(deploymentId);
        return "redirect:/processDefinitionList";
    }

}
