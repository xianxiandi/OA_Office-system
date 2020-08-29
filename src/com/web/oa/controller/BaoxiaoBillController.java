package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.service.BaoxiaoService;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class BaoxiaoBillController {

    @Autowired
    private BaoxiaoService baoxiaoService;
    @Autowired
    private WorkFlowService workFlowService;

    @RequestMapping("/main")
    public String main(ModelMap model){
      ActiveUser activeUser= (ActiveUser)SecurityUtils.getSubject().getPrincipal();
      model.addAttribute("activeUser",activeUser);
      return "index";
    }
    @RequestMapping("/myBaoxiaoBill")
    public String myBaoxiaoBill(ModelMap model){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<BaoxiaoBill> list=baoxiaoService.findmyBaoxiaoBillList(activeUser.getId());
        model.addAttribute("baoxiaoList",list);
        return "baoxiaobill";
    }
    @RequestMapping("/viewHisComment")
    public String viewHisComment(long id,ModelMap model){
       BaoxiaoBill bill= baoxiaoService.findBaoxiaoBillById(id);
       model.addAttribute("baoxiaoBill",bill);
     List<Comment> commentList= workFlowService.findCommentByBaoxiaoBillId(id);
     model.addAttribute("commentList",commentList);
        return "workflow_commentlist";
    }

    @RequestMapping("/viewCurrentImageByBill")
    public String viewCurrentImageByBill(Long billId,ModelMap model){
        String BUSSINESS_KEY= Constants.BAOXIAO_KEY+"."+billId;
       Task task= workFlowService.findTaskByBussinessKey(BUSSINESS_KEY);

        ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(task.getId());
        model.addAttribute("deploymentId",pd.getDeploymentId());
        model.addAttribute("imageName",pd.getDiagramResourceName());
        Map<String,Object> map=workFlowService.findCoordingByTaskId(task.getId());
        model.addAttribute("acs",map);
        return "viewimage";

    }
    @RequestMapping("/leaveBillAction_delete")
    public String leaveBillAction_delete(String id){
        baoxiaoService.deleteBaoxiaoBillById(Long.parseLong(id));
        return "leaveBillAction_delete";
    }


}
